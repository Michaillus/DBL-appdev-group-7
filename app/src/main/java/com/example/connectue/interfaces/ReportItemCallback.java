package com.example.connectue.interfaces;

import com.google.firebase.firestore.QueryDocumentSnapshot;

public interface ReportItemCallback {
    public void itemClicked(QueryDocumentSnapshot content);
}
