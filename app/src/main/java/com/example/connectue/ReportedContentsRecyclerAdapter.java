package com.example.connectue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class ReportedContentsRecyclerAdapter extends RecyclerView.Adapter<ReportedContentsRecyclerAdapter.MyViewHolder> {
    private List<QueryDocumentSnapshot> reports;
    private ReportItemCallback callback;
    public ReportedContentsRecyclerAdapter(List<QueryDocumentSnapshot> reports, ReportItemCallback callback) {
        this.reports = reports;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ReportedContentsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_report, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QueryDocumentSnapshot content = reports.get(position);
        holder.reportId.setText(content.getString(General.REPORTCONTENTID));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.itemClicked(content);
            }
        });
    }


    @Override
    public int getItemCount() {
        return reports.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView reportId;
        ConstraintLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            reportId = itemView.findViewById(R.id.report_content_id_tv);
            layout = itemView.findViewById(R.id.one_report_request);
        }
    }
}
