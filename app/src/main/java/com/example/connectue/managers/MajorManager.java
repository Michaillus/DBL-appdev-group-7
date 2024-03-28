package com.example.connectue.managers;

import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.model.Major;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MajorManager extends EntityManager<Major> {

    /**
     * Constructor for major manager given instance of  FireStore database and the name of
     * majors collection in that database.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of majors collection in the database.
     */
    public MajorManager(FirebaseFirestore db, String collectionName) {
        super(db, collectionName);
    }

    @Override
    protected Major deserialize(DocumentSnapshot document) {

        Major major;
        if ((document.getLong(Major.RATING_SUM_ATTRIBUTE) == null) ||
                (document.getLong(Major.RATING_NUMBER_ATTRIBUTE) == null)) {
            major = new Major(document.getString(Major.MAJOR_NAME_ATTRIBUTE),
                    document.getString(Major.MAJOR_CODE_ATTRIBUTE));
            set(major, document.getId(), new ItemUploadCallback() {
                @Override
                public void onSuccess() {}
            });
        } else {
            major = new Major(
                    document.getId(),
                    document.getString(Major.MAJOR_NAME_ATTRIBUTE),
                    document.getString(Major.MAJOR_CODE_ATTRIBUTE),
                    document.getLong(Major.RATING_SUM_ATTRIBUTE),
                    document.getLong(Major.RATING_NUMBER_ATTRIBUTE));
        }
        return major;
    }

    @Override
    protected Map<String, Object> serialize(Major major) {
        Map<String, Object> majorData = new HashMap<>();

        majorData.put(Major.MAJOR_NAME_ATTRIBUTE, major.getMajorName());
        majorData.put(Major.MAJOR_CODE_ATTRIBUTE, major.getMajorCode());
        majorData.put(Major.RATING_SUM_ATTRIBUTE, major.getRatingSum());
        majorData.put(Major.RATING_NUMBER_ATTRIBUTE, major.getRatingNumber());

        return majorData;
    }
}
