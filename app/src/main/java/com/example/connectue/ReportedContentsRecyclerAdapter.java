package com.example.connectue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class ReportedContentsRecyclerAdapter extends RecyclerView.Adapter<ReportedContentsRecyclerAdapter.MyViewHolder> {
    private List<String> reportIds;
    private Context context;
    public ReportedContentsRecyclerAdapter(List<String> reportIds, Context context) {
        this.reportIds = reportIds;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportedContentsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_report, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.reportId.setText(reportIds.get(position));
    }


    @Override
    public int getItemCount() {
        return reportIds.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView reportId;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            reportId = itemView.findViewById(R.id.report_content_id_tv);
        }
    }
}
