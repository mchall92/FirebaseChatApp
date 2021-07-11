package neu.edu.madcourse.firebasechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import neu.edu.madcourse.firebasechatapp.Adapters.MessageAdapter;
import neu.edu.madcourse.firebasechatapp.Fragments.APIService;
import neu.edu.madcourse.firebasechatapp.Model.Chat;
import neu.edu.madcourse.firebasechatapp.Model.Users;
import neu.edu.madcourse.firebasechatapp.Notification.Client;
import neu.edu.madcourse.firebasechatapp.Notification.Data;
import neu.edu.madcourse.firebasechatapp.Notification.MyResponse;
import neu.edu.madcourse.firebasechatapp.Notification.Sender;
import neu.edu.madcourse.firebasechatapp.Notification.Token;
import neu.edu.madcourse.firebasechatapp.Utils.ImageSelector;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView username;
    private ImageView imageView;

    private FirebaseUser firebaseUser;
    private DatabaseReference ref;

    private Intent intent;
    private String userId;

    private RecyclerView messageRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private List<Chat> mChatList;

    // sticker buttons
    private CircularImageView soccerButton;
    private CircularImageView kiteButton;
    private CircularImageView telescopeButton;
    private CircularImageView bikeButton;
    private CircularImageView hotAirButton;
    private CircularImageView kayakButton;

    // selected sticker
    private int selectedNum = 0;

    private ImageButton messageSendButton;

    // Notification
    APIService apiService;
    boolean notify = false;

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

        // notification
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

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

        // set up sticker buttons
        soccerButton = findViewById(R.id.message_activity_soccer);
        kiteButton = findViewById(R.id.message_activity_kite);
        telescopeButton = findViewById(R.id.message_activity_telescope);
        bikeButton = findViewById(R.id.message_activity_bike);
        hotAirButton = findViewById(R.id.message_activity_hot_air);
        kayakButton = findViewById(R.id.message_activity_kayak);

        soccerButton.setBorderWidth(15f);
        kiteButton.setBorderWidth(15f);
        telescopeButton.setBorderWidth(15f);
        bikeButton.setBorderWidth(15f);
        hotAirButton.setBorderWidth(15f);
        kayakButton.setBorderWidth(15f);

        soccerButton.setOnClickListener(this);
        kiteButton.setOnClickListener(this);
        telescopeButton.setOnClickListener(this);
        bikeButton.setOnClickListener(this);
        hotAirButton.setOnClickListener(this);
        kayakButton.setOnClickListener(this);


        // send sticker button
        messageSendButton = findViewById(R.id.message_activity_send_button);
        messageSendButton.setOnClickListener(this);

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

        final String msg = message;

        ref = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (notify) {
                    sendNotification(receiver, users.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    private void sendNotification(String receiver, String username, String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot sn : snapshot.getChildren()) {
                    Token token = sn.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher,
                            username + " send you a sticker, go check it out! ", "New Message", userId);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotifications(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body() == null) {
                                            Toast.makeText(MessageActivity.this, "Failed Notification", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
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

    @Override
    public void onClick(View v) {
        resetStickers();
        switch (v.getId()) {
            case (R.id.message_activity_send_button):
                notify = true;
                if (selectedNum > 0) {
                    sendMessage(firebaseUser.getUid(), userId, String.valueOf(selectedNum));
                } else {
                    Toast.makeText(MessageActivity.this,
                            "Please Select A Sticker", Toast.LENGTH_LONG).show();
                }
                break;
            case (R.id.message_activity_bike):
                bikeButton.setBorderColor(Color.DKGRAY);
                selectedNum = 1;
                break;
            case (R.id.message_activity_kayak):
                kayakButton.setBorderColor(Color.DKGRAY);
                selectedNum = 2;
                break;
            case (R.id.message_activity_soccer):
                soccerButton.setBorderColor(Color.DKGRAY);
                selectedNum = 3;
                break;
            case (R.id.message_activity_telescope):
                telescopeButton.setBorderColor(Color.DKGRAY);
                selectedNum = 4;
                break;
            case (R.id.message_activity_kite):
                kiteButton.setBorderColor(Color.DKGRAY);
                selectedNum = 5;
                break;
            case (R.id.message_activity_hot_air):
                hotAirButton.setBorderColor(Color.DKGRAY);
                selectedNum = 6;
                break;
        }
    }

    private void resetStickers() {
        soccerButton.setBorderColor(Color.WHITE);
        kiteButton.setBorderColor(Color.WHITE);
        telescopeButton.setBorderColor(Color.WHITE);
        bikeButton.setBorderColor(Color.WHITE);
        hotAirButton.setBorderColor(Color.WHITE);
        kayakButton.setBorderColor(Color.WHITE);
    }
}