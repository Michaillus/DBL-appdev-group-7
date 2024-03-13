package com.example.connectue;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.PropertyName;

/**
 * Class for Post object to store strings that need to be displayed on screen.
 */
public class Post {
    //use same name as we given while uploading post

    @PropertyName("publisher")
    public String uid;
    @PropertyName("text")
    public String pDescription;
    @PropertyName("photoULR")
    public String pImage;
    @PropertyName("likes")
    public int pLikes;
    @PropertyName("comments")
    public int pComments;

    public Post() {
    }

    public Post(String uid, String pDescription, String pImage, int pLikes, int pComments) {
        this.uid = uid;
        this.pDescription = pDescription;
        this.pImage = pImage;
        this.pLikes = pLikes;
        this.pComments = pComments;
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

    public boolean isLiked() {

        return false;
    }
//
//    public String getpLikes() {
//        return pLikes;
//    }
//
//    public void setpLikes(String pLikes) {
//        this.pLikes = pLikes;
//    }
//
//    public String getpComments() {
//        return pComments;
//    }
//
//    public void setpComments(String pComments) {
//        this.pComments = pComments;
//    }
}
