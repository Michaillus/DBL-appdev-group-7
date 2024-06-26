package com.example.connectue.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.connectue.utils.AdminReportedItemDataManager;
import com.example.connectue.utils.General;
import com.example.connectue.R;
import com.example.connectue.interfaces.ReportItemCallback;
import com.example.connectue.adapters.ReportedContentsAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;

/**
 * The workbench for administrator to see the reported content, and choose to delete or keep
 *  the contents.
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageReportHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageReportHistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;
    private List<QueryDocumentSnapshot> reports;
    private String currentChannel = General.POSTCOLLECTION;
    private DocumentSnapshot currentDocument = null;
    private boolean isImageFetchable = false;
    private String imageURL = "";
    private static final String DEFAULT_CONTENT = "Content Here";
    private static final String DEFAULT_COUNT = "Number of reports: ";
    private Button postChannelBtn;
    private Button commentChannelBtn;
    private Button courseReviewChannelBtn;
    private Button deleteBtn;
    private Button keepBtn;
    private ImageView contentIV;
    private TextView contentTV;
    private TextView contentInfoTV;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    //The data manager for administrator workbench.
    AdminReportedItemDataManager adminReportedItemDataManager;

    /**
     * Constructor.
     */
    public ManageReportHistoryFragment() {
        // Required empty public constructor
        reports = new ArrayList<>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageReportHistoryFragment.
     */
    public static ManageReportHistoryFragment newInstance(String param1, String param2) {
        ManageReportHistoryFragment fragment = new ManageReportHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This is the auto-generated functions.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * The auto-generated function, where all components will be initialized.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_report_history, container, false);
        initComponents(view);
        initRecyclerReview(view);

        adminReportedItemDataManager =
                new AdminReportedItemDataManager(reports, db, currentChannel, this);
        adminReportedItemDataManager.loadReportedContents();

        initPostButton();
        initCommentButton();
        initCourseReviewButton();

        return view;
    }

    /**
     * Initialize all components on this fragment, including all buttons, text fields,
     *  and picture views.
     * @param view the view where all components resides.
     */
    private void initComponents(View view) {
        db = FirebaseFirestore.getInstance();
        postChannelBtn = view.findViewById(R.id.report_load_post_btn);
        commentChannelBtn = view.findViewById(R.id.report_load_comment_btn);
        courseReviewChannelBtn = view.findViewById(R.id.report_load_course_review_btn);
        deleteBtn = view.findViewById(R.id.report_delete_btn);
        keepBtn = view.findViewById(R.id.report_keep_btn);
        contentIV = view.findViewById(R.id.report_picture_IV);
        contentTV = view.findViewById(R.id.report_text_content_tv);
        contentInfoTV = view.findViewById(R.id.report_basic_info_tv);

        contentTV.setText(DEFAULT_CONTENT);

        contentInfoTV.setText(DEFAULT_COUNT);

        setDefaultContentIV();
    }

    /**
     * Initialize the recycler view for to do list.
     * @param view the view where recycler view resides.
     */
    private void initRecyclerReview(View view) {
        recyclerView = view.findViewById(R.id.admin_reported_RV);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ReportedContentsAdapter(reports, new ReportItemCallback() {
            @Override
            public void itemClicked(QueryDocumentSnapshot content) {
                adminReportedItemDataManager.loadContentToWorkBench(content);
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Once the data in the list of the adapter changed, update contents in the
     *  adapter.
     */
    public void updateDataInAdapter(){
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Once the clicked data has been fetched from the FireStore, this method will receive
     *  changes and update related fields.
     * @param currentDocument the DocumentSnapshot storing the information of the clicked item,
     * @param imageURL the image URL of the reported contents.
     * @param textContent the text content of the reported contents.
     * @param count the number of reported times.
     */
    public void updateWorkbench(DocumentSnapshot currentDocument
            , String imageURL, String textContent, String count){
        this.currentDocument = currentDocument;
        this.imageURL = imageURL;

        setContentIV(imageURL);
        contentTV.setText(textContent);
        contentInfoTV.setText(DEFAULT_COUNT + count);
        setDeleteKeepButton();
    }

    /**
     * set the image view to the default picture, and disable the click listener.
     */
    private void setDefaultContentIV() {
        contentIV.setImageResource(R.drawable.baseline_person_24);
        contentIV.setOnClickListener(null);
    }

    /**
     * Fill in the picture into the image view using its URL.
     * @param url the URL link of the picture.
     */
    private void setContentIV(String url) {
        Glide.with(getContext()).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                setDefaultContentIV();
                isImageFetchable = false;
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                isImageFetchable = true;
                return false;
            }
        }).into(contentIV);
    }

    /**
     * Add click listener to the keep and delete button. Activate the function of them.
     *  But if the currentDocument is null or does not exist, activation will be skipped.
     */
    private void setDeleteKeepButton() {
        if (currentDocument == null || !currentDocument.exists()) {
            disableDeleteKeepButton();
            return;
        }
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminReportedItemDataManager.deleteContent(isImageFetchable, imageURL);
            }
        });
        keepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminReportedItemDataManager.removeRequest();
            }
        });
    }

    /**
     * Deactivate the delete and keep buttons.
     */
    private void disableDeleteKeepButton() {
        deleteBtn.setOnClickListener(null);
        keepBtn.setOnClickListener(null);
    }

    /**
     * reset all the variables.
     */
    public void resetData() {
        currentDocument = null;
        isImageFetchable = false;
        imageURL = "";
        setDefaultContentIV();
        contentTV.setText(DEFAULT_CONTENT);
        contentInfoTV.setText(DEFAULT_COUNT);
        disableDeleteKeepButton();
    }

    /**
     * Initialize the post button.
     */
    private void initPostButton() {
        postChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchChannel(General.POSTCOLLECTION);
            }
        });
    }

    /**
     * Initialize the comment button.
     */
    private void initCommentButton() {
        commentChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchChannel(General.COMMENTCOLLECTION);
            }
        });
    }

    /**
     * Initialize the course review button.
     */
    private void initCourseReviewButton() {
        courseReviewChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchChannel(General.COURSEREVIEWCOLLECTION);
            }
        });
    }

    /**
     * Switch to other types of reported contents.
     * @param channel the channel where the reported contents will be in.
     */
    private void switchChannel(String channel) {
        currentChannel = channel;
        adminReportedItemDataManager.setCurrentChannel(currentChannel);
        adminReportedItemDataManager.resetAfterDeleteKeep();
        resetData();
    }
}