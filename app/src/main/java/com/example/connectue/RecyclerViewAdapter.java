package com.example.connectue;

import android.content.Context;
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

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    List<Post> postList;
    Context context;

    public RecyclerViewAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_history_post,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.postTxt.setText(postList.get(position).getText());

        String index = String.valueOf(position);
        holder.deletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "delete test success" + index,
                        Toast.LENGTH_SHORT).show();
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
