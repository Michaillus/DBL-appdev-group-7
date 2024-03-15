package com.example.connectue;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.databinding.RowCommentBinding;
import com.example.connectue.databinding.RowPostsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class for using recyclerView to display comments.
 */
public class AdapterComments extends RecyclerView.Adapter<AdapterComments.CommentHolder>{

    private Context context;
    List<Comment> commentList;
    private FirebaseFirestore db;

    private String currentUid;

    private String TAG = "TestAdapterComments";

    public AdapterComments(Context context, List<Comment> commentList) {
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

        private TextView publisherName;
        private TextView commentDescription;
        private TextView publishTime;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            publisherName = itemView.findViewById(R.id.publisherNameCommentTv);
            publishTime = itemView.findViewById(R.id.publishTimeCommentTv);
            commentDescription = itemView.findViewById(R.id.commentDescriptionTv);
        }

        public void bind(Comment comment) {
            publisherName.setText(comment.getPublisherName());
            publishTime.setText(TimeUtils.getTimeAgo(comment.gettimestamp()));
            commentDescription.setText(comment.getContent());

        }
    }
}
