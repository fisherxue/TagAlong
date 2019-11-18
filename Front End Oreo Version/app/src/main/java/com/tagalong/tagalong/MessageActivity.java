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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private Context context;
    private final String TAG = "ChatActivity";

    private ImageButton sendMessage;
    private EditText messageToSend;
    private Profile profile;
    private String ID;

    private MessageAdapter messageAdapter;
    private Chat chat;
    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        context = this;
        profile = (Profile) getIntent().getSerializableExtra("profile");
        ID =  getIntent().getStringExtra("ID");
        sendMessage = (ImageButton) findViewById(R.id.sendButton);
        messageToSend = (EditText) findViewById(R.id.sendMessage);

        LocalBroadcastManager.getInstance(context).registerReceiver((receiver),
                new IntentFilter(FirebaseMessagingServiceHandler.REQUEST_ACCEPT)
        );
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
    }

    private void sendMSG(String message){
        Conversation newConversation = new Conversation(profile.getUserID(),message);

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getString(R.string.sendMessage);
        final Gson gson = new Gson();
        final String conversationJson = gson.toJson(newConversation);
        JSONObject conversationJsonObject;

        try {
            conversationJsonObject = new JSONObject(conversationJson);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,conversationJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Received List of Messages for the chat");
                    setConversationList(response);
                    initChatView();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: Could not send message");
                    Log.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(context, "We encountered some error,\nPlease send your message again", Toast.LENGTH_LONG).show();
                }
            });

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


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

    private void setConversationList (JSONObject response){
        chat = new Chat(ID);
        chat.setUserID(profile.getUserID());
        JSONArray conversationListIn;
        JSONArray userListIn;
        List<Conversation> conversationList = new ArrayList<>();
        List<String> usernames = new ArrayList<>();
        try {
            conversationListIn = response.getJSONArray("chat"); // ASK IAN FOR CORRECT NAME
            userListIn = response.getJSONArray("usernames");
            for (int i = 0; i < conversationListIn.length(); i++){
                Conversation conversationNew = new Conversation(conversationListIn.getJSONObject(i).getString("userID"),
                        conversationListIn.getJSONObject(i).getString("message"));
                conversationList.add(conversationNew);
            }
            for (int i = 0; i < userListIn.length(); i++) {
                String username = userListIn.getString(i);
                usernames.add(username);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        chat.setConversationList(conversationList);
        chat.setUsernames(usernames);
    }

    private void initChat(){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getString(R.string.getChatList);
        final Gson gson = new Gson();
        final String chatJson = gson.toJson(this.chat);
        JSONObject chatJsonObject;

        try {
            chatJsonObject = new JSONObject(chatJson);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, chatJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Received List of Messages for the chat");
                    setConversationList(response);
                    initChatView();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: Could not get Chat");
                    Log.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(context, "We encountered some error,\nPlease reload the page", Toast.LENGTH_LONG).show();
                }
            });

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }
}
