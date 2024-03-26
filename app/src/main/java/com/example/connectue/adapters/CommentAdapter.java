package com.example.connectue.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connectue.R;
import com.example.connectue.interfaces.DownloadItemCallback;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.User2;
import com.example.connectue.utils.General;
import com.example.connectue.utils.TimeUtils;
import com.example.connectue.model.Comment;
import com.google.firebase.auth.FirebaseAuth;
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
        private ImageView profilePic;
        private TextView commentDescription;
        private TextView publishTime;
        private ImageButton reportBtn;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            publisherName = itemView.findViewById(R.id.publisherNameCommentTv);
            profilePic = itemView.findViewById(R.id.profilePicCommentIv);
            publishTime = itemView.findViewById(R.id.publishTimeCommentTv);
            commentDescription = itemView.findViewById(R.id.commentDescriptionTv);
            reportBtn = itemView.findViewById(R.id.reportCommentBtn);

            userManager = new UserManager(FirebaseFirestore.getInstance(),
                    User2.USER_COLLECTION_NAME);
        }

        public void bind(Comment comment) {
            userManager.downloadOne(comment.getPublisherId(), new DownloadItemCallback<User2>() {
                @Override
                public void onSuccess(User2 data) {
                    // Load user name
                    publisherName.setText(data.getFullName());
                    String imageUrl = data.getProfilePicUrl();

                    // Load profile picture
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(profilePic.getContext()).load(imageUrl).into(profilePic);
                        profilePic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                General.showPopupWindow(profilePic, imageUrl);
                            }
                        });
                    }
                }
            });

            publishTime.setText(TimeUtils.getTimeAgo(comment.getTimestamp()));
            commentDescription.setText(comment.getText());

            userManager.downloadOne(comment.getPublisherId(), new DownloadItemCallback<User2>() {
                @Override
                public void onSuccess(User2 data) {
                    // Set comment user name
                    publisherName.setText(data.getFullName());
                }
            });

            // Initialize report button only for verified users
            currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userManager.downloadOne(currentUid, new DownloadItemCallback<User2>() {
                @Override
                public void onSuccess(User2 data) {
                    // Check current user role
                    if (data.getRole() == General.GUEST) {
                        reportBtn.setVisibility(View.GONE);
                    } else {
                        reportBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                General.reportOperation(itemView.getContext(), General.COMMENTCOLLECTION, comment.getParentId());
                            }
                        });
                    }
                }
            });
        }
    }
}
