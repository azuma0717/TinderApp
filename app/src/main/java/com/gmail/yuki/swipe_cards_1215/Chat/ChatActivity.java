package com.gmail.yuki.swipe_cards_1215.Chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.yuki.swipe_cards_1215.Matches.MatchesActivity;
import com.gmail.yuki.swipe_cards_1215.Matches.MatchesAdapter;
import com.gmail.yuki.swipe_cards_1215.Matches.MatchesObject;
import com.gmail.yuki.swipe_cards_1215.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private EditText mSendEditText;
    private Button mSendButton;

    private String currentUserId, matchId, chatId, profileImageUrl;

    DatabaseReference mDatabaseUser, mDatabaseChat, mMatchUser;

    ChatObject chatObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchId = getIntent().getExtras().getString("matchId");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        mMatchUser = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId);

        getChatId();


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = findViewById(R.id.message);
        mSendButton = findViewById(R.id.send);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendボタンを押したら、ファンクション発動
                sendMessage();
            }
        });

        getChatMessage();

    }

    //メッセージをセットしてデータベースに送る。送信後はテキストボックスをnullにする
    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();

        if (!sendMessageText.isEmpty()) {
            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createByUser", currentUserId);
            newMessage.put("text", sendMessageText);

            newMessageDb.setValue(newMessage);
        }

        mSendEditText.setText(null);


    }

    //chatIDをゲットする。呼び出し元は、onCreate///////////////////////////////////////
    private void getChatId() {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessage();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getChatMessage() {

        mMatchUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String key = dataSnapshot.getKey();

                if (key.equals("profileImageUrl")) {
                    profileImageUrl = dataSnapshot.getValue(String.class);
                    chatObject.setProfileImageUrl(profileImageUrl);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                if (dataSnapshot.exists()) {
                    String message = null;
                    String createByUser = null;

                    if (dataSnapshot.child("text").getValue() != null) {
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("createByUser").getValue() != null) {
                        createByUser = dataSnapshot.child("createByUser").getValue().toString();
                    }


                    if (message != null && createByUser != null) {
                        Boolean currentUserBoolean = false;
                        if (createByUser.equals(currentUserId)) {
                            currentUserBoolean = true;
                        }

                        ChatObject newMessage = new ChatObject(message, currentUserBoolean);
                        resultChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    ///////////////////////////////////////////////////////////////////////////////

    private ArrayList<ChatObject> resultChat = new ArrayList<ChatObject>();

    private List<ChatObject> getDataSetChat() {
        return resultChat;
    }
}
