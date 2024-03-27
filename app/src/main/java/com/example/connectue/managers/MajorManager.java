package com.example.connectue.managers;

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
        return new Major(
                document.getString(Major.MAJOR_NAME_ATTRIBUTE),
                document.getString(Major.MAJOR_CODE_ATTRIBUTE));
    }

    @Override
    protected Map<String, Object> serialize(Major major) {
        Map<String, Object> majorData = new HashMap<>();

        majorData.put(Major.MAJOR_NAME_ATTRIBUTE, major.majorName);
        majorData.put(Major.MAJOR_CODE_ATTRIBUTE, major.majorCode);

        return majorData;
    }
}
