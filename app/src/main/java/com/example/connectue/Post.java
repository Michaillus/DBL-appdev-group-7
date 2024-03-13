package com.example.connectue;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

/**
 * Class for Post object to store strings that need to be displayed on screen.
 */
public class Post {
    //use same name as we given while uploading post

    private String uid;

    private String description;

    private String imageUrl;

    private int likeNumber;

    private int commentNumber;

    private String postID;

    public Post() {
    }

    public Post(String uid, String description, String imageUrl, int likeNumber, int commentNumber, String postID) {
        this.uid = uid;
        this.description = description;
        this.imageUrl = imageUrl;
        this.likeNumber = likeNumber;
        this.commentNumber = commentNumber;
        this.postID = postID;
    }

    /**
     * Automatically called method for AdapterPosts
     * @param view placeholder for the post image.
     * @param imageUrl image URL
     */
    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            view.setVisibility(View.VISIBLE);
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .into(view);
        } else {
            // Optionally, load a placeholder image or clear the ImageView
            // Example: view.setImageResource(R.drawable.placeholder_image);
            view.setImageDrawable(null); // Clear the ImageView
            view.setVisibility(View.GONE);
        }
    }

    // Getters and setters for post ID.
    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    // Getters and setters for user ID.
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Getters and setters for post description.
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getters and setters for post image URL.
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String pImage) {
        this.imageUrl = pImage;
    }

    // Getters and setters for number of likes on the post.
    public int getLikeNumber() {
        return likeNumber;
    }

    public void incrementLikeNumber() {
        this.likeNumber++;
    }

    public void decrementLikeNumber() {
        this.likeNumber--;
    }

    // Getters and setters for number of comments on the post.
    public int getCommentNumber() {
        return commentNumber;
    }

    public void incrementCommentNumber() {
        this.commentNumber++;
    }

    public void decrementCommentNumber() {
        this.commentNumber--;
    }
}
