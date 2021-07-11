package neu.edu.madcourse.firebasechatapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import neu.edu.madcourse.firebasechatapp.Adapters.UserAdapter;
import neu.edu.madcourse.firebasechatapp.Model.ChatList;
import neu.edu.madcourse.firebasechatapp.Model.Users;
import neu.edu.madcourse.firebasechatapp.Notification.Token;
import neu.edu.madcourse.firebasechatapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<Users> mUserList;

    private FirebaseUser firebaseUser;
    private DatabaseReference ref;

    private List<ChatList> mUserChatList;

    private RecyclerView chatFragmentRecyclerview;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        chatFragmentRecyclerview = view.findViewById(R.id.chat_fragment_recyclerview);
        chatFragmentRecyclerview.setHasFixedSize(true);
        chatFragmentRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mUserChatList = new ArrayList<>();

        ref = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(firebaseUser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mUserChatList.clear();

                // find all users
                for (DataSnapshot sn : snapshot.getChildren()) {
                    ChatList chatList = sn.getValue(ChatList.class);
                    mUserChatList.add(chatList);
                }
                buildChatList();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                // Get new FCM registration token
                updateToken(task.getResult());
            }
        });

        return view;
    }

    private void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token updatedToken = new Token(token);
        ref.child(firebaseUser.getUid()).setValue(updatedToken);
    }

    private void buildChatList() {

        // Get all the most recent chats
        mUserList = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("MyUsers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mUserList.clear();

                for (DataSnapshot sn : snapshot.getChildren()) {
                    Users users = sn.getValue(Users.class);

                    for (ChatList chatList : mUserChatList) {
                        if (users.getId().equals(chatList.getId())) {
                            mUserList.add(users);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUserList);
                chatFragmentRecyclerview.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
}