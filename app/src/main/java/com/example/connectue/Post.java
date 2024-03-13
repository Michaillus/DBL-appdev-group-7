package com.example.connectue;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

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

    public String postID;

    public Post() {
    }

    public Post(String uid, String pDescription, String pImage, int pLikes, int pComments, String postID) {
        this.uid = uid;
        this.pDescription = pDescription;
        this.pImage = pImage;
        this.pLikes = pLikes;
        this.pComments = pComments;
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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an ImageView with the same drawable as the clicked ImageView
                    ImageView fullImageView = new ImageView(v.getContext());
                    fullImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    fullImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    // Load the full-size image into the ImageView
                    Glide.with(v.getContext())
                            .load(imageUrl) // Load the same image URL
                            .into(fullImageView);

                    // Create a popup window to display the full-size image
                    PopupWindow popupWindow = new PopupWindow(fullImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                    // Close the popup window if the full imageview is clicked.
                    fullImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                }
            });
        } else {
            // Optionally, load a placeholder image or clear the ImageView
            // Example: view.setImageResource(R.drawable.placeholder_image);
            view.setImageDrawable(null); // Clear the ImageView
            view.setVisibility(View.GONE);
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
