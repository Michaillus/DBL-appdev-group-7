package com.example.connectue.interfaces;

import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Each item in the to do list of the admin mode, once it is clicked, pass QueryDocumentSnapshot of
 *  this item to the upper class to do the further operations.
 */
public interface ReportItemCallback {
    /**
     *  Let the other class to implement what to do next once this item is clicked.
     * @param content QueryDocumentSnapshot storing the information of this item.
     *                Note, this content is the document storing the report information.
     */
    public void itemClicked(QueryDocumentSnapshot content);
}
