package com.example.connectue.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.R;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.Comment;
import com.example.connectue.model.User;
import com.example.connectue.utils.TimeUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Class for using recyclerView to display replies.
 */
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyHolder>{

    private Context context;
    List<Comment> commentList;
    private FirebaseFirestore db;

    public ReplyAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ReplyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_comment.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_replies, parent, false);
        return new ReplyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    //view holder class
    public class ReplyHolder extends RecyclerView.ViewHolder {

        UserManager userManager;

        private TextView replierName;

        private TextView replyText;
        private TextView replyTime;


        public ReplyHolder(@NonNull View itemView) {
            super(itemView);

            replierName = itemView.findViewById(R.id.replierName);

            replyTime = itemView.findViewById(R.id.replyTime);
            replyText = itemView.findViewById(R.id.replyText);

            userManager = new UserManager(FirebaseFirestore.getInstance(),
                    User.USER_COLLECTION_NAME);
        }

        public void bind(Comment comment) {
            userManager.downloadOne(comment.getPublisherId(), new ItemDownloadCallback<User>() {
                @Override
                public void onSuccess(User data) {
                    // Load user name
                    replierName.setText(data.getFullName());
                }
            });

            replyTime.setText(TimeUtils.getTimeAgo(comment.getDatetime()));
            replyText.setText(comment.getText());

            userManager.downloadOne(comment.getPublisherId(), new ItemDownloadCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    // Set comment user name
                    replierName.setText(user.getFullName());
                }
            });

        }
    }
}
