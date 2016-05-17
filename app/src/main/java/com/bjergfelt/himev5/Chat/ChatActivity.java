package com.bjergfelt.himev5.Chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.Util.Config;
import com.bjergfelt.himev5.Util.MyApplication;
import com.bjergfelt.himev5.Util.NotificationUtils;
import com.bjergfelt.himev5.model.User;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Socket;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

/*
    Using Native Socket.IO and Android by  Naoyuki Kanezawa - 20 January 2015
    http://socket.io/blog/native-socket-io-and-android/

    We are using a NodeJS as backend for our chat system.
    Our Android App will be a chat client that communicates with a Socket.IO Node.JS chat server.



 */




public class ChatActivity extends AppCompatActivity {

    private com.github.nkzawa.socketio.client.Socket mSocket;

    private String TAG = ChatActivity.class.getSimpleName();

    private String chatRoomId;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message>  messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private Button btnSend;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
                mSocket = IO.socket("http://192.168.1.9");

                //IO.socket() returns a socket for the url with default options.
                //the method caches the result, so you always get a same Socket instance for an url from any Activity or Fragment.

        } catch (URISyntaxException e){}

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (Button) findViewById(R.id.btn_send);
        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");
        String title = intent.getStringExtra("name");

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();

        //Self user id is to identify the message owner
//        String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();
        String selfUserId = "anders1";
       // mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);
        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList,selfUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)){
                    handlePushNotification(intent);
                }
            }
        };

        btnSend.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                attemptSend();

            }
        });

        //Socket.IO is bidirectional, which means we can send events to the server, but also at any time during the communication the server can send events to us.
        //We can make the socket listen on an event
        //mSocket.on("new message", onNewMessage);

        //Establish the connection here
        mSocket.connect();
        mSocket.on("new message", onNewMessage);




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    protected void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
    }


    @Override
    protected void onResume() {
        super.onResume();

        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        //NotificationUtils.clearNotifications();
    }


    private void handlePushNotification(Intent intent) {
    Message message = (Message) intent.getSerializableExtra("message");
        String chatRoomId = intent.getStringExtra("chat_room_id");

        if (message != null && chatRoomId != null){
            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1){
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,null, mAdapter.getItemCount() - 1);
            }
        }

    }

    /*
    Emitting events
    Sending data
    First a String, but we can change it to JSON with org.json package (Should we?)
    */
    private void attemptSend(){
        String message = inputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        this.inputMessage.setText("");
        Message chatMessage = new Message();
        chatMessage.setId("1st");
        chatMessage.setMessage(message);
        chatMessage.setCreatedAt(message);
        User user = new User();
        user.setName("Anders");
        user.setId("anders1");
        chatMessage.setUser(user);
        chatMessage.setMessage(message);
        messageArrayList.add(chatMessage);
        mSocket.emit("new message", message);
    }


    /*
        Listening on events
        listen on the "new message" event to recieve messages from other users

     */
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   JSONObject data = (JSONObject) args[0];
                   String username;
                   String message;
                   Message chatMessage = new Message();
                   User user = new User();
                   user.setId("anders1");

                   chatMessage.setId("1st");

                   try {
                       username = data.getString("username");
                       message = data.getString("message");
                       Log.e(TAG, username + " " + message);
                       user.setName(username);
                       chatMessage.setMessage(message);
                       chatMessage.setUser(user);

                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
                    //add the message to view
                   messageArrayList.add(chatMessage);
                   mAdapter.notifyDataSetChanged();
                   if (mAdapter.getItemCount() > 1) {
                       // scrolling to bottom of the recycler view
                       recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                   }
               }
           });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }



}
