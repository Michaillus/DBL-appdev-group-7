package com.example.connectue.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;

import com.example.connectue.R;
import com.example.connectue.activities.CourseViewActivity;
import com.example.connectue.activities.MaterialPDFActivity;
import com.example.connectue.managers.UserManager;
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.model.Material;
import com.example.connectue.model.Post;
import com.example.connectue.model.Review;
import com.example.connectue.model.User2;
import com.example.connectue.utils.General;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MyViewHolder> {

    Context context; // for inflation
    List<Material> materialModels;
    private String TAG = "MaterialAdapter: ";

    public MaterialAdapter(Context context, List<Material> materialModels) {
        this.context = context;
        this.materialModels = materialModels;
    }

    @NonNull
    @Override
    public MaterialAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where we inflate the layout (Giving a look to our rows)

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.material_row, parent, false);

        return new MaterialAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialAdapter.MyViewHolder holder, int position) {
        // assign values to the views we created in the course_review_row file
        // based on the position of the recycler view
        Material material = materialModels.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MaterialPDFActivity.class);
                Log.d(TAG, material.getDocUrl().toString());
                Log.d(TAG, String.valueOf(position));
                String docUrl = materialModels.get(position).getDocUrl();
                intent.putExtra("docURL", docUrl);
                view.getContext().startActivity(intent);

            }
        });


        holder.userManager.downloadOne(material.getPublisherId(),
                new FireStoreDownloadCallback<User2>() {
                    @Override
                    public void onSuccess(User2 user) {
                        holder.uName.setText(user.getFirstName());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error getting the user", e);
                    }});
        holder.caption.setText(materialModels.get(position).getText());
        holder.date.setText(materialModels.get(position).getDatetime().toString());
        holder.like.setImageResource(R.drawable.like_icon);
        holder.dislike.setImageResource(R.drawable.dislike);
        holder.likeNum.setText(String.valueOf(materialModels.get(position).getLikeNumber()));
        holder.dislikeNum.setText(String.valueOf(materialModels.get(position).getDislikeNumber()));
    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items you want to display
        return materialModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // grab the views from course_review_row file

        ImageView like, dislike;
        TextView uName, caption, date;
        TextView likeNum, dislikeNum;
        UserManager userManager;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            like = itemView.findViewById(R.id.likePostBtn);
            dislike = itemView.findViewById(R.id.dislikeReview);
            uName = itemView.findViewById(R.id.uName);
            caption = itemView.findViewById(R.id.materialCaption);
            likeNum = itemView.findViewById(R.id.likeNum);
            dislikeNum = itemView.findViewById(R.id.dislikeNum);

            userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
        }


    }
}

