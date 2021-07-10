package neu.edu.madcourse.firebasechatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import neu.edu.madcourse.firebasechatapp.Model.Chat;
import neu.edu.madcourse.firebasechatapp.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private static final int NUM_STICKERS = 6;

    private Context chatContext;
    private List<Chat> mChatList;

    private FirebaseUser firebaseUser;

    public MessageAdapter(Context chatContext, List<Chat> mChatList) {
        this.chatContext = chatContext;
        this.mChatList = mChatList;
    }

    @NonNull
    @NotNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(chatContext)
                    .inflate(R.layout.chat_message_right, parent, false);
        } else {
            view = LayoutInflater.from(chatContext)
                    .inflate(R.layout.chat_message_left, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapter.MessageViewHolder holder, int position) {
        Chat chat = mChatList.get(position);

        int num = (chat.getMessage().charAt(0) - '0') % NUM_STICKERS;
        switch (num) {
            case (1):
                holder.message.setImageResource(R.drawable.bicycle);
                break;
            case (2):
                holder.message.setImageResource(R.drawable.kayak);
                break;
            case (3):
                holder.message.setImageResource(R.drawable.soccer);
                break;
            case (4):
                holder.message.setImageResource(R.drawable.telescope);
                break;
            case (5):
                holder.message.setImageResource(R.drawable.kite);
                break;
            case (0):
                holder.message.setImageResource(R.drawable.hot_air_balloon);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private ImageView message;

        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.show_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChatList.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
