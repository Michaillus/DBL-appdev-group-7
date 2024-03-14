package com.example.connectue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PostFragment extends Fragment {

    private TextView publisherName;
    private TextView publisherTime;
    private ImageView postImage;
    private TextView postDescription;
    private ImageButton likePostBtn;
    private EditText addComment;
    private ImageButton sendCommentBtn;

    public PostFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        publisherName = view.findViewById(R.id.publisherNameTextView);
        publisherTime = view.findViewById(R.id.publishTimePostTextView);
        postImage = view.findViewById(R.id.postImageInPostImageView);
        postDescription = view.findViewById(R.id.postDescriptionTextView);
        likePostBtn = view.findViewById(R.id.likeAPostBtn);
        addComment = view.findViewById(R.id.addPostCommentET);
        sendCommentBtn = view.findViewById(R.id.sendPostCommentBtn);
    }
}
