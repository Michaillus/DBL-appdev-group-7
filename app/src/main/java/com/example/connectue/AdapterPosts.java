package com.example.connectue;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterPosts {

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        //views from row_posts.xml
        ImageView uPictureIv, pImageIv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
