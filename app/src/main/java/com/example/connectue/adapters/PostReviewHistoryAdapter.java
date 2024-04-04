package com.example.connectue.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connectue.R;
import com.example.connectue.model.Interactable;
import com.example.connectue.model.Post;
import com.example.connectue.utils.General;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * The PostReviewHistoryAdapter class is responsible for managing the RecyclerView adapter
 * to display post history in a RecyclerView.
 * It provides functionality to delete posts and display post details.
 */
public class PostReviewHistoryAdapter extends RecyclerView.Adapter<PostReviewHistoryAdapter.MyViewHolder> {
    List<Interactable> postList;
    Context context;
    String collectionName;

    private FirebaseFirestore db;

    /**
     * Constructs a new PostReviewHistoryAdapter with the given list of historical posts,
     * context, and collection name.
     *
     * @param postList       a list of posts in post history of the user
     * @param context        context reference
     * @param collectionName the name of the collection in Firestore
     */
    public PostReviewHistoryAdapter(List<Interactable> postList, Context context, String collectionName) {
        this.postList = postList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        this.collectionName = collectionName;
    }

    /**
     * Creates a new MyViewHolder instance by inflating the layout for each item in the RecyclerView.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new MyViewHolder that holds the inflated view.
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_history_post,parent,false);
        return new MyViewHolder(view);
    }

    /**
     * Binds the data to the views in the RecyclerView.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Interactable post = postList.get(position);
        holder.postTxt.setText(post.getText());

        String index = String.valueOf(position);

        // Set click listener for delete button
        holder.deletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("delete", "post ID: " + post.getId());
                db.collection(collectionName).document(post.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("delete", "DocumentSnapshot successfully deleted!");
                                Log.i("delete", "Delete content: " + post.getText());
                                Log.i("delete", collectionName);
                                // remove the deleted post from postList
                                postList.remove(post);
                                notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("delete", "Error deleting document", e);
                            }
                        });
            }
        });

        if (post instanceof Post) {
            Log.i("Post history", "This is instance of Post ");
            Log.i("Post history", "URL: " + ((Post)postList.get(position)).getImageUrl());
            Log.i("Post history", "text: " + postList.get(position).getText());
            Glide.with(this.context).load(((Post)postList.get(position)).getImageUrl()).into(holder.postPic);
        } else {
            Log.i("Post history", "This NOT is instance of Post ");
        }
    }

    /**
     * Returns the total number of historical posts in the data set held by the adapter.
     *
     * @return The total number of historical posts.
     */
    @Override
    public int getItemCount() {
        return postList.size();
    }


    /**
     * Inner class to hold references to views within each item of the RecyclerView.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView postPic;
        TextView postTxt;
        Button deletBtn;

        /**
         * Constructs a new MyViewHolder with the given itemView.
         *
         * @param itemView The view for each item in the RecyclerView.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            postPic = itemView.findViewById(R.id.post_pic);
            postTxt = itemView.findViewById(R.id.post_txt);
            deletBtn = itemView.findViewById(R.id.post_del_btn);

        }
    }
}
