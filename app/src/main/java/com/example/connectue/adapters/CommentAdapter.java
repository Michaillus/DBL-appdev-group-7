package com.example.connectue.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.R;
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.User2;
import com.example.connectue.utils.TimeUtils;
import com.example.connectue.model.Comment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Class for using recyclerView to display comments.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder>{

    private Context context;
    List<Comment> commentList;
    private FirebaseFirestore db;

    private String currentUid;

    private String TAG = "TestAdapterComments";

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_comment.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    //view holder class
    public class CommentHolder extends RecyclerView.ViewHolder {

        UserManager userManager;

        private TextView publisherName;
        private TextView commentDescription;
        private TextView publishTime;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            publisherName = itemView.findViewById(R.id.publisherNameCommentTv);
            publishTime = itemView.findViewById(R.id.publishTimeCommentTv);
            commentDescription = itemView.findViewById(R.id.commentDescriptionTv);

            userManager = new UserManager(FirebaseFirestore.getInstance(),
                    User2.USER_COLLECTION_NAME);
        }

        public void bind(Comment comment) {
            publishTime.setText(TimeUtils.getTimeAgo(comment.getTimestamp()));
            commentDescription.setText(comment.getText());

            userManager.downloadOne(comment.getPublisherId(),
                    user -> publisherName.setText(user.getFullName()));
        }
    }
}
