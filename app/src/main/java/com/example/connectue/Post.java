package com.example.connectue;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Class for Post object to store strings that need to be displayed on screen.
 */
public class Post {

    private static final String TAG = "Post class: ";

    private String publisherName;
    private String description;
    private String imageUrl;
    private int likeNumber;
    private int commentNumber;
    private String postID;

    public Post() {
    }

    public Post(String publisherName, String description, String imageUrl, int likeNumber, int commentNumber, String postID) {
        this.publisherName = publisherName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.likeNumber = likeNumber;
        this.commentNumber = commentNumber;
        this.postID = postID;
    }

    public static void createPost(QueryDocumentSnapshot document, PostCreateCallback callback) {
        // Handling documents with a null field
        if (document.getString("publisher") == null) {
            String m = "Post publisher should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (document.getString("text") == null) {
            String m = "Post text should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (document.getLong("likes") == null) {
            String m = "Number of post likes should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (document.getLong("comments") == null) {
            String m = "Number of post comments should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        User.fetchUserName(document.getString("publisher"), new UserNameCallback() {
            @Override
            public void onUserNameFetched(String userName) {
                Post post = new Post(userName,
                        document.getString("text"),
                        document.getString("photoURL"),
                        document.getLong("likes").intValue(),
                        document.getLong("comments").intValue(),
                        document.getId());
                callback.onPostCreated(post);
            }
        });
    }

    /**
     * Automatically called method for AdapterPosts
     * (DESCRIBE WHAT THE METHOD IS DOING)
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

    // Getters and setters for post ID.
    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    // Getters and setters for user ID.
    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
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
