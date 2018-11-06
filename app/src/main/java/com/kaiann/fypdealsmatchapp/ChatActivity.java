package com.kaiann.fypdealsmatchapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaiann.fypdealsmatchapp.Model.Messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    private String mPartnerId;
    private Toolbar mChatToolbar;
    private Button mChatSendBtn;
    private EditText mChatMessageView;

    private RecyclerView mMessagesList;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private DatabaseReference mRootRef;
    FirebaseAuth mAuth;
    String mCurrentUserId;
    String user1, user2;
    public String chatPartner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mPartnerId = getIntent().getStringExtra("partner_id");
        user1 = getIntent().getStringExtra("user1");
        user2 = getIntent().getStringExtra("user2");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mChatToolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mChatToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        mChatSendBtn = findViewById(R.id.chat_send_btn);
        mChatMessageView = findViewById(R.id.chat_message_view);

        mAdapter = new MessageAdapter(messagesList);

        mMessagesList = findViewById(R.id.messages_list);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

        if(mCurrentUserId.equals(user1)){
            chatPartner = user2;
        }else if(mCurrentUserId.equals(user2)){
            chatPartner = user1;
        }

        loadMessages();

        //display item name as chat name
        mRootRef.child("Partner").child(mPartnerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String chat_name = dataSnapshot.child("itemId").getValue(String.class);
                mRootRef.child("Item").child(chat_name).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String item_chat_name = dataSnapshot.child("name").getValue(String.class);
                        getSupportActionBar().setTitle(item_chat_name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(mCurrentUserId.equals(user1)){
            chatPartner = user2;
        }else if(mCurrentUserId.equals(user2)){
            chatPartner = user1;
        }

        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(mCurrentUserId)){
                    Map chatUserMap = new HashMap();
                    //tutorial 28 do we need to add partnerId?
                        chatUserMap.put("Chat/" + mCurrentUserId + "/" + chatPartner, false);
                        chatUserMap.put("Chat/" + chatPartner +"/"+ mCurrentUserId, false);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError !=null){
                                Toast.makeText(ChatActivity.this, "error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //onclick listener for chat message send button
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });



    }

    private void loadMessages() {

        DatabaseReference chatroomRef = mRootRef.child("messages").child(mCurrentUserId).child(chatPartner);

        chatroomRef.limitToLast(15).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message = dataSnapshot.getValue(Messages.class);

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.smoothScrollToPosition(messagesList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage() {

        DatabaseReference userName = mRootRef.child("Users").child(mCurrentUserId);
        userName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_name;
                user_name = dataSnapshot.child("name").getValue(String.class);

                String message = user_name + ":  " + mChatMessageView.getText().toString();

                if(!TextUtils.isEmpty(message)){

                    String current_user_ref = "messages/" + mCurrentUserId + "/" + chatPartner;
                    String chat_user_ref = "messages/" + chatPartner + "/" + mCurrentUserId;

                    DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserId)
                            .child(chatPartner).push();
                    String push_id = user_message_push.getKey();

                    Map messageMap = new HashMap();
                    messageMap.put("message", message);
                    messageMap.put("from", mCurrentUserId);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref+ "/" + push_id, messageMap);

                    mChatMessageView.setText("");

                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError !=null){
                                Toast.makeText(ChatActivity.this, "error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
