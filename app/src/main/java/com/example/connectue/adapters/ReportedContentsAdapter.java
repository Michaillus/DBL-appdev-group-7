package com.example.connectue.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.utils.General;
import com.example.connectue.R;
import com.example.connectue.interfaces.ReportItemCallback;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

/**
 * Adapter class for displaying reported contents in a RecyclerView.
 */
public class ReportedContentsAdapter extends RecyclerView.Adapter<ReportedContentsAdapter.MyViewHolder> {
    private List<QueryDocumentSnapshot> reports; // List to hold the reported contents
    private ReportItemCallback callback; // Callback interface for item click events

    /**
     * Constructor for ReportedContentsAdapter.
     * @param reports List of QueryDocumentSnapshot objects representing reported contents
     * @param callback Callback interface for handling item click events
     */
    public ReportedContentsAdapter(List<QueryDocumentSnapshot> reports, ReportItemCallback callback) {
        this.reports = reports;
        this.callback = callback;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ReportedContentsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single report item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_report, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QueryDocumentSnapshot content = reports.get(position);
        holder.reportId.setText(content.getString(General.REPORTCONTENTID));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            // Invoke callback method when a report item is clicked
            public void onClick(View v) {
                callback.itemClicked(content);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return reports.size();
    }

    /**
     * ViewHolder class to hold views for individual report items.
     */
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
