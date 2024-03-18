package com.example.connectue.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connectue.R;
import com.example.connectue.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import javax.security.auth.callback.Callback;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    List<Post> postList;
    Context context;

    private FirebaseFirestore db;

    public RecyclerViewAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_history_post,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.postTxt.setText(post.getText());

        String index = String.valueOf(position);
        holder.deletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("delete", "post ID: " + post.getId());
                db.collection("posts").document(post.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("delete", "DocumentSnapshot successfully deleted!");
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

        Glide.with(this.context).load(postList.get(position).getImageUrl()).into(holder.postPic);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    //reference to each row
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView postPic;
        TextView postTxt;
        Button deletBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            postPic = itemView.findViewById(R.id.post_pic);
            postTxt = itemView.findViewById(R.id.post_txt);
            deletBtn = itemView.findViewById(R.id.post_del_btn);

        }
    }
}
