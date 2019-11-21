package com.tagalong.tagalong;

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
import android.util.TimingLogger;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private Context context;
    private static final String TAG = "MessageActivity";

    private ImageButton sendMessage;
    private EditText messageToSend;
    private Profile profile;
    private String ID;
    private Chat chat;
    private BroadcastReceiver receiver;
    private TimingLogger timingLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timingLogger = new TimingLogger(TAG, "Message Activity");
        setContentView(R.layout.activity_message);
        Log.d(TAG, "message activity created");
        context = this;
        profile = (Profile) getIntent().getSerializableExtra("profile");
        ID =  getIntent().getStringExtra("ID");
        sendMessage = (ImageButton) findViewById(R.id.sendButton);
        messageToSend = (EditText) findViewById(R.id.sendMessage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        chat = new Chat(ID);
        chat.setUserID(profile.getUserID());
        sendMessage.setOnClickListener(new View.OnClickListener() {
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
        initChat();

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

    private void sendMSG(String message){
        Log.d(TAG, "Sending Message");
        Conversation newConversation = new Conversation(message);
        newConversation.setRoomID(ID);
        newConversation.setUserID(profile.getUserID());
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getString(R.string.sendMessage);
        final Gson gson = new Gson();
        final String conversationJson = gson.toJson(newConversation);
        JSONObject conversationJsonObject;
        timingLogger.addSplit("Sending Message");
        try {
            conversationJsonObject = new JSONObject(conversationJson);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,conversationJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    timingLogger.addSplit("Sent Message Successfully");
                    Log.d(TAG, "Message Sent successfully");
                    Log.d(TAG, "Received List of Messages for the chat");
                    setConversationList(response);
                    initChatView();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    timingLogger.addSplit("Error Sending Message");
                    Log.d(TAG, "Error: Could not send message");
                    Log.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(context, "We encountered some error,\nPlease send your message again", Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("userID",profile.getUserID());
                    return headers;
                }
            };

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            Log.d(TAG, "JsonException building conversation " + e.getMessage());
        }

    }


    private void initChatView(){
        Log.d(TAG,"initializing TripView");
        timingLogger.addSplit("init chat view - start adapter");
        timingLogger.dumpToLog();
        timingLogger.reset();
        RecyclerView recyclerView = findViewById(R.id.message_recycler_view);
        recyclerView.setHasFixedSize(true);
        MessageAdapter messageAdapter = new MessageAdapter(context, chat.getConversationList(), profile);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setConversationList (JSONObject response){
        Log.d(TAG,"Setting chat list");
        timingLogger.addSplit("Starting to setup messages");
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
        timingLogger.addSplit("done setting up messages");
    }

    private void initChat(){
        Log.d(TAG, "Initializing Chat");
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getString(R.string.getChatList);
        final Gson gson = new Gson();
        final String chatJson = gson.toJson(this.chat);
        JSONObject chatJsonObject;

        try {
            timingLogger.addSplit("getting list of messages");
            Log.d(TAG, "Retrieving list of messages");
            chatJsonObject = new JSONObject(chatJson);
            Log.d(TAG,chatJsonObject.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, chatJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Received List of Messages for the chat");
                    timingLogger.addSplit("received list of messages");
                    setConversationList(response);
                    initChatView();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: Could not get Chat");
                    timingLogger.addSplit("error receiving list of messages");
                    Log.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(context, "We encountered some error,\nPlease reload the page", Toast.LENGTH_LONG).show();
                }
            });

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            Log.d(TAG, "JsonException building conversation " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }
}
