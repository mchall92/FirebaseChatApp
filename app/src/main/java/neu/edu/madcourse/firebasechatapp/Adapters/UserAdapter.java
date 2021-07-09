package neu.edu.madcourse.firebasechatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import neu.edu.madcourse.firebasechatapp.MessageActivity;
import neu.edu.madcourse.firebasechatapp.Model.Users;
import neu.edu.madcourse.firebasechatapp.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UsersViewHolder> {

    private Context usersContext;
    private List<Users> mUsers;

    public UserAdapter(Context usersContext, List<Users> mUsers) {
        this.usersContext = usersContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @NotNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(usersContext)
                .inflate(R.layout.item_user, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.UsersViewHolder holder, int position) {
        Users users = mUsers.get(position);
        holder.username.setText(users.getUsername());
        holder.image.setImageResource(R.mipmap.ic_launcher);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(usersContext, MessageActivity.class);
                intent.putExtra("userId", users.getId());
                usersContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class UsersViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView image;

        public UsersViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.users_recyclerview_username);
            image = itemView.findViewById(R.id.users_recyclerview_img);
        }
    }
}
