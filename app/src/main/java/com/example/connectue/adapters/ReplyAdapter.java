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

    /**
     * Constructor for ReplyAdapter.
     * @param context The context of the activity or fragment.
     * @param commentList List of comments to display as replies.
     */
    public ReplyAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ReplyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_comment.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_replies, parent, false);
        return new ReplyHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReplyHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    /**
     * ViewHolder class for ReplyAdapter.
     */
    public class ReplyHolder extends RecyclerView.ViewHolder {

        UserManager userManager;

        private TextView replierName;

        private TextView replyText;
        private TextView replyTime;


        /**
         * Constructor for ReplyHolder.
         * @param itemView The view associated with the ViewHolder.
         */
        public ReplyHolder(@NonNull View itemView) {
            super(itemView);

            replierName = itemView.findViewById(R.id.replierName);

            replyTime = itemView.findViewById(R.id.replyTime);
            replyText = itemView.findViewById(R.id.replyText);

            userManager = new UserManager(FirebaseFirestore.getInstance(),
                    User.USER_COLLECTION_NAME);
        }

        /**
         * Binds the comment data to the views.
         * @param comment The comment object to bind.
         */
        public void bind(Comment comment) {
            userManager.downloadOne(comment.getPublisherId(), new ItemDownloadCallback<User>() {
                @Override
                public void onSuccess(User data) {
                    // Load user name
                    replierName.setText(data.getFullName());
                }
            });

            // Set the text of the reply time TextView to a human-readable format
            // representing the time elapsed since the comment was posted.
            replyTime.setText(TimeUtils.getTimeAgo(comment.getDatetime()));
            // Set the text of the replyText TextView to the text content of the comment.
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
