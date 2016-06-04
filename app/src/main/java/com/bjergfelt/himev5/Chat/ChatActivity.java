package com.bjergfelt.himev5.Chat;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bjergfelt.himev5.R;
import com.github.nkzawa.socketio.client.IO;

import java.net.URISyntaxException;
import java.util.ArrayList;




public class ChatActivity extends AppCompatActivity {

    private com.github.nkzawa.socketio.client.Socket mSocket;

    private String TAG = ChatActivity.class.getSimpleName();

    private String chatRoomId;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message>  messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView inputMessage;
    private Button btnSend;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
                mSocket = IO.socket("http://192.168.1.9");

                //IO.socket() returns a socket for the url with default options.
                //the method caches the result, so you always get a same Socket instance for an url from any Activity or Fragment.

        } catch (URISyntaxException e){}

        inputMessage = (TextView) findViewById(R.id.textView5);
        btnSend = (Button) findViewById(R.id.accept_applicant_button);
        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");
        String title = intent.getStringExtra("name");

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messageArrayList = new ArrayList<>();

        //Self user id is to identify the message owner
//        String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();
        String selfUserId = "anders1";
       // mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);
        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList,selfUserId);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }




}
