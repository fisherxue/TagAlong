package com.tagalong.tagalong.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.tagalong.tagalong.communication.FirebaseCallback;
import com.tagalong.tagalong.FirebaseMessagingServiceHandler;
import com.tagalong.tagalong.adapter.MessageAdapter;
import com.tagalong.tagalong.models.Chat;
import com.tagalong.tagalong.models.Conversation;
import com.tagalong.tagalong.models.Profile;
import com.tagalong.tagalong.R;
import com.tagalong.tagalong.communication.VolleyCallback;
import com.tagalong.tagalong.communication.VolleyCommunicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * View for the Messaging (Chat) Screen
 * Uses MessageAdapter
 */
public class MessageActivity extends AppCompatActivity {

  private Context context;
  private static final String TAG = "MessageActivity";

  private ImageButton sendMessageButton;
  private EditText messageToSend;
  private Profile profile;
  private String ID;
  private Chat chat;
  private BroadcastReceiver receiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message);
    Log.d(TAG, "message activity created");
    context = this;
    sendMessageButton = (ImageButton) findViewById(R.id.sendButton);
    messageToSend = (EditText) findViewById(R.id.sendMessage);
    profile = (Profile) getIntent().getSerializableExtra("profile");
    // If null profile, load it from internal memory.
    if (profile == null) {
      loadProfile();
    } else {
      setup();
    }
  }

  /**
   * Set up the basic global modules for activity
   */
  private void setup (){
    ID =  getIntent().getStringExtra("ID");
    chat = new Chat(ID);
    chat.setUserID(profile.getUserID());
    initChat();
  }

  @Override
  protected void onStart() {
    super.onStart();
    sendMessageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!messageToSend.getText().toString().equals("")){
          sendMSG(messageToSend.getText().toString());
        }
        else {
          Toast.makeText(context,"Please Enter Message",Toast.LENGTH_SHORT).show();
        }
        messageToSend.setText("");
      }
    });

    // Re-initiate the the list of messages based on broadcast received on chat notification
    receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        initChat();
      }
    };

    LocalBroadcastManager.getInstance(context).registerReceiver((receiver),
            new IntentFilter(FirebaseMessagingServiceHandler.REQUEST_ACCEPT)
    );
  }

  /**
   * Post a new message to
   * @param message message to send
   */
  private void sendMSG(String message){
    Log.d(TAG, "Sending Message");
    Conversation newConversation = new Conversation(message);
    newConversation.setRoomID(ID);
    newConversation.setUserID(profile.getUserID());

    String url = getString(R.string.sendMessage);

    final Gson gson = new Gson();
    final String conversationJson = gson.toJson(newConversation);
    JSONObject conversationJsonObject;

    VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
    VolleyCallback callback = new VolleyCallback() {
      @Override
      public void onSuccess(JSONObject response){
        Log.d(TAG, "Message sent successfully");
        Log.d(TAG, "Received list of messages for the chat");
        setConversationList(response);
        initChatView();
      }

      @Override
      public void onError(String result){
        Log.d(TAG, "Could not send message");
        Log.d(TAG, "Error: " + result);
        Toast.makeText(context, "We encountered some error,\nPlease send your message again", Toast.LENGTH_LONG).show();
      }

    };
    try {
      conversationJsonObject = new JSONObject(conversationJson);
      communicator.volleyPost(url,conversationJsonObject,callback);
    } catch (JSONException e) {
      Log.d(TAG, "Error making conversation JSONObject");
      Log.d(TAG, "JSONException: " + e.toString());
      e.printStackTrace();
    }
  }

  /**
   * Start the message adapter load the chat view.
   */
  private void initChatView(){
    Log.d(TAG,"initializing TripView");
    RecyclerView recyclerView = findViewById(R.id.message_recycler_view);
    recyclerView.setHasFixedSize(true);
    MessageAdapter messageAdapter = new MessageAdapter(context, chat.getConversationList(), profile);
    recyclerView.setAdapter(messageAdapter);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
    linearLayoutManager.setStackFromEnd(true);
    recyclerView.setLayoutManager(linearLayoutManager);
  }

  /**
   * Set list of conversations received from Get call
   * @param response response from get call
   */
  private void setConversationList (JSONObject response){
    Log.d(TAG,"Setting chat list");
    chat = new Chat(ID);
    chat.setUserID(profile.getUserID());
    JSONArray conversationListIn;
    JSONArray userListIn;
    List<Conversation> conversationList = new ArrayList<>();
    List<String> usernames = new ArrayList<>();
    try {
      conversationListIn = response.getJSONArray("messages"); // ASK IAN FOR CORRECT NAME
      userListIn = response.getJSONArray("users");
      for (int i = 0; i < conversationListIn.length(); i++){

        Conversation conversationNew = new Conversation(conversationListIn.getJSONObject(i).getString("message"));
        conversationNew.setUserName(conversationListIn.getJSONObject(i).getString("username"));
        conversationList.add(conversationNew);
      }
      for (int i = 0; i < userListIn.length(); i++) {
        String username = userListIn.getString(i);
        usernames.add(username);
      }
    } catch (JSONException e) {
      Log.d(TAG, "JsonException building conversation " + e.getMessage());
    }
    chat.setConversationList(conversationList);
    chat.setUsernames(usernames);
  }

  /**
   * Call Get to get a list of messages from chat database.
   */
  private void initChat(){
    Log.d(TAG, "Initializing Chat");
    String url = getString(R.string.getChatList);
    HashMap<String, String> headers = new HashMap<String, String>();
    headers.put("userID",profile.getUserID());
    headers.put("roomID",ID);
    VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
    VolleyCallback callback = new VolleyCallback() {
      @Override
      public void onSuccess(JSONObject response){
        Log.d(TAG, "Received list of messages for the chat");
        setConversationList(response);
        initChatView();
      }
      @Override
      public void onError(String result){
        Log.d(TAG, "Could not get chat");
        Log.d(TAG, "Error: " +  result);
        Toast.makeText(context, "We encountered some error,\nPlease reload the page", Toast.LENGTH_LONG).show();
      }
    };

    Log.d(TAG, "Retrieving list of messages");
    communicator.volleyGet(url,callback,headers);
  }

  @Override
  protected void onStop() {
    super.onStop();
    LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
  }

  /**
   * Loads up the profile from internal memory of app.
   */
  private void loadProfile(){
    String profileFilename = "Saved_Profile.txt";
    StringBuffer stringBuffer = new StringBuffer();
    try (BufferedReader reader =
                 new BufferedReader(new InputStreamReader(openFileInput(profileFilename)))) {
      String line = reader.readLine();
      while (line != null) {
        stringBuffer.append(line).append('\n');
        line = reader.readLine();
      }
    } catch (IOException e) {
      Log.d(TAG, "Could not load a saved profile.");
      Toast.makeText(context, "Sorry we faced some issue,\n Please reload the page", Toast.LENGTH_LONG).show();
      return ;
    }

    String contents = stringBuffer.toString();
    JSONObject profileJSON;
    try {
      this.profile = new Profile();
      profileJSON = new JSONObject(contents);
      profile.setUserID(profileJSON.getString("userID"));
      profile.setUsername(profileJSON.getString("username"));
      profile.setFirstName(profileJSON.getString("firstName"));
      profile.setLastName(profileJSON.getString("lastName"));
      profile.setAge(profileJSON.getInt("age"));
      profile.setGender(profileJSON.getString("gender"));
      profile.setEmail(profileJSON.getString("email"));
      profile.setDriver(profileJSON.getBoolean("isDriver"));
      profile.setJoinedDate(profileJSON.getString("joinedDate"));
      profile.setCarCapacity(profileJSON.getInt("carCapacity"));
      JSONArray jsonArray = profileJSON.getJSONArray("interests");
      int [] interests = new int[jsonArray.length()];
      for (int i = 0; i < jsonArray.length(); i++){
        interests[i] = jsonArray.getInt(i);
      }
      profile.setInterests(interests);

      FirebaseCallback firebaseCallback = new FirebaseCallback() {
        @Override
        public void onSuccess(@NonNull Task<InstanceIdResult> task) {
          String token = task.getResult().getToken();
          profile.setFbToken(token);
          loginSavedProfile();
        }
      };
      getFCMToken(firebaseCallback);
    } catch (JSONException e) {
      Toast.makeText(context, "Sorry we faced some issue,\n Please reload the page", Toast.LENGTH_LONG).show();
      Log.d(TAG, "Failed to convert stored json string to profile");
      Log.d(TAG, ("JSONException: " + e.toString()));
      this.profile = null;
      return ;
    }
  }

  /**
   * Get the FCM token from FireBase micro service
   * @param firebaseCallback the call back functionality module for FireBase calls.
   */
  private void getFCMToken(final FirebaseCallback firebaseCallback){
    FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
              @Override
              public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                  Log.w(TAG, "FCM failed", task.getException());
                  return;
                }
                Log.d(TAG,"Got FCM Device Token");
                firebaseCallback.onSuccess(task);
              }
            });
  }

  /**
   * Make a put request to update the user profile (with new FCM token).
   */
  private void loginSavedProfile(){
    String url = getString(R.string.updateProfile);
    Gson gson = new Gson();
    String profileJson = gson.toJson(profile);
    JSONObject profileJsonObject;
    VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
    VolleyCallback callback = new VolleyCallback() {
      @Override
      public void onSuccess(JSONObject response){
        setup();
      }

      @Override
      public void onError(String result){
        Toast.makeText(context, "Sorry we faced some issue,\n Please reload the page", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Saved login verification not successful");
        Toast.makeText(context, "Please try again", Toast.LENGTH_LONG).show();
      }
    };

    try {
      profileJsonObject = new JSONObject((profileJson));
      communicator.volleyPut(url,profileJsonObject,callback);
    } catch (JSONException e) {
      Log.d(TAG, "Error making login JSONObject");
      Log.d(TAG, "JSONException: " + e.toString());
      e.printStackTrace();
    }
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent(context, HomeActivity.class);
    intent.putExtra("profile",profile);
    startActivity(intent);
    MessageActivity.this.finish();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
  }
}