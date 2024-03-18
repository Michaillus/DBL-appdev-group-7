package com.example.connectue.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;

import com.example.connectue.R;
import com.example.connectue.model.Question;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {

    Context context;
    ArrayList<Question> questionModels;

    public QuestionAdapter(Context context, ArrayList<Question> questionModels) {
        this.context = context;
        this.questionModels = questionModels;
    }

    @NonNull
    @Override
    public QuestionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_question_row, parent, false);
        return new QuestionAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.MyViewHolder holder, int position) {
        holder.uName.setText(questionModels.get(position).getuName());
        holder.date.setText(questionModels.get(position).getDate());
        holder.review.setText(questionModels.get(position).getuText());
        holder.like.setImageResource(questionModels.get(position).getqLikes());
        holder.likeNum.setText(questionModels.get(position).getqLikeNum());

    }

    @Override
    public int getItemCount() {
        return questionModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView like;
        TextView uName, review, likeNum, date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            like = itemView.findViewById(R.id.likePostBtn);
            uName = itemView.findViewById(R.id.uName);
            review = itemView.findViewById(R.id.review);
            likeNum = itemView.findViewById(R.id.likeNum);
            date = itemView.findViewById(R.id.date);
        }
    }
}
