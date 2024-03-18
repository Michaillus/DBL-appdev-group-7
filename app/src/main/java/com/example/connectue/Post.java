package com.example.connectue;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Class for Post object to store strings that need to be displayed on screen.
 */
public class Post extends Interactable {

    private static final String TAG = "Post class: ";

    private String imageUrl;

    public Post(String postId, String publisherId, String text, String imageUrl,
                Long likeNumber, Long commentNumber) throws IllegalArgumentException {

        super(postId, publisherId, text, likeNumber, 0L, commentNumber);

        this.setImageUrl(imageUrl);

    }

    /**
     * Automatically called method for AdapterPosts to load the imageURL into the imageView.
     * When the imageView is clicked, the full size image is popup.
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

    // Getters and setters for post image URL.
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String pImage) {
        this.imageUrl = pImage;
    }
}