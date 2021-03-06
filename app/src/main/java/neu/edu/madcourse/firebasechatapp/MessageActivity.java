package neu.edu.madcourse.firebasechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import neu.edu.madcourse.firebasechatapp.Adapters.MessageAdapter;
import neu.edu.madcourse.firebasechatapp.Model.Chat;
import neu.edu.madcourse.firebasechatapp.Model.Users;
import neu.edu.madcourse.firebasechatapp.Utils.ImageSelector;

public class MessageActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView username;
    ImageView imageView;

    FirebaseUser firebaseUser;
    DatabaseReference ref;

    Intent intent;
    String userId;

    RecyclerView messageRecyclerview;
    LinearLayoutManager linearLayoutManager;
    MessageAdapter messageAdapter;
    List<Chat> mChatList;

    EditText messageEditText;
    ImageButton messageSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //widgets
        username = findViewById(R.id.message_activity_username);
        imageView = findViewById(R.id.message_activity_profile_img);
        toolbar = findViewById(R.id.message_activity_toolbar);

        intent = getIntent();
        userId = intent.getStringExtra("userId");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("MyUsers").child(userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                assert user != null;
                username.setText(user.getUsername());
                ImageSelector.select(imageView, user.getUsername());

                readMessage(firebaseUser.getUid(), userId);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        messageSendButton = findViewById(R.id.message_activity_send_button);
        messageEditText = findViewById(R.id.message_activity_message_edit_text);

        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (message.length() > 0) {
                    sendMessage(firebaseUser.getUid(), userId, message);
                } else {
                    Toast.makeText(MessageActivity.this, "failed", Toast.LENGTH_LONG).show();
                }
                messageEditText.setText("");
            }
        });

        // Recyclerview
        messageRecyclerview = findViewById(R.id.message_activity_recyclerview);
        messageRecyclerview.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerview.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MessageActivity.this, LoginActivity.class));
                finish();
                return true;
        }
        return false;
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("message", message);

        ref.child("Chats").push().setValue(map);

        // add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase
                .getInstance()
                .getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    private void readMessage(String myId, String userId) {
        mChatList = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mChatList.clear();
                for (DataSnapshot sn : snapshot.getChildren()) {
                    Chat chat = sn.getValue(Chat.class);

                    if ((chat.getReceiver().equals(myId) && chat.getSender().equals(userId))
                    || (chat.getReceiver().equals(userId) && chat.getSender().equals(myId))) {
                        mChatList.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(MessageActivity.this, mChatList);
                messageRecyclerview.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
}