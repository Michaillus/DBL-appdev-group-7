package com.example.connectue;

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
    public String uName;
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

    public Post(String uName, String pDescription, String pImage, int pLikes, int pComments) {
        this.uName = uName;
        this.pDescription = pDescription;
        this.pImage = pImage;
        this.pLikes = pLikes;
        this.pComments = pComments;
    }

//    public String getuName() {
//        return uName;
//    }
//
//    public void setuName(String uName) {
//        this.uName = uName;
//    }
//
//    public String getpDescription() {
//        return pDescription;
//    }
//
//    public void setpDescription(String pDescription) {
//        this.pDescription = pDescription;
//    }
//
//    public String getpImage() {
//        return pImage;
//    }
//
    public void setImageUrl(String pImage) {
        this.pImage = pImage;
    }

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .into(view);
        } else {
            // Optionally, load a placeholder image or clear the ImageView
            // Example: view.setImageResource(R.drawable.placeholder_image);
            view.setImageDrawable(null); // Clear the ImageView
        }
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
