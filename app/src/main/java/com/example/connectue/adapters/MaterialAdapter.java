package com.example.connectue.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.R;
import com.example.connectue.activities.MaterialPDFActivity;
import com.example.connectue.managers.UserManager;
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.model.Material;
import com.example.connectue.model.User2;
import com.example.connectue.utils.TimeUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MyViewHolder> {

    List<Material> materialList;
    private static final String TAG = "MaterialAdapter: ";

    private FragmentManager fragmentManager;

    public MaterialAdapter(List<Material> materialList, FragmentManager fragmentManager) {
        this.materialList = materialList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MaterialAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where we inflate the layout (Giving a look to our rows)

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.material_row, parent, false);

        return new MaterialAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialAdapter.MyViewHolder holder, int position) {
        // assign values to the views we created in the course_review_row file
        // based on the position of the recycler view
        Material material = materialList.get(position);
        holder.bind(material);
    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items you want to display
        return materialList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // grab the views from course_review_row file

        ImageView like, dislike;
        TextView publisherName, caption, date;
        TextView likeNumber, dislikeNumber;
        UserManager userManager;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            like = itemView.findViewById(R.id.likePostBtn);
            dislike = itemView.findViewById(R.id.dislikeReview);
            publisherName = itemView.findViewById(R.id.uName);
            caption = itemView.findViewById(R.id.materialCaption);
            likeNumber = itemView.findViewById(R.id.likeNum);
            dislikeNumber = itemView.findViewById(R.id.dislikeNum);

            userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
        }

        public void bind(Material material) {

            // Set publisher name
            userManager.downloadOne(material.getPublisherId(),
                    new FireStoreDownloadCallback<User2>() {
                        @Override
                        public void onSuccess(User2 user) {
                            publisherName.setText(user.getFirstName());
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "Error getting the user", e);
                        }
                    });

            // Set main text
            caption.setText(material.getText());

            // Set publication date
            date.setText(TimeUtils.getTimeAgo(material.getDatetime()));

            // Set like icon
            like.setImageResource(R.drawable.like_icon);

            // Set dislike icon
            dislike.setImageResource(R.drawable.dislike);

            // Set like number
            likeNumber.setText(String.valueOf(material.getLikeNumber()));

            // Set dislike number
            dislikeNumber.setText(String.valueOf(material.getDislikeNumber()));

            // Set listener for opening the download document page
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MaterialPDFActivity.class);
                    String docUrl = material.getDocUrl();
                    intent.putExtra("docURL", docUrl);
                    view.getContext().startActivity(intent);

                }
            });

        }
    }
}

