//package com.example.connectue;
//
//import android.util.Log;
//
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//
//public class PostList extends InteractableList {
//
//    private static final String TAG = "PostList: ";
//
//    ArrayList<Post> list;
//
//    public PostList() {
//        super();
//        list = new ArrayList<>();
//    }
//
//    /**
//     * Displays posts that were loaded from the database to the post feed in UI.
//     * @param snapshot object containing all the posts that were loaded from database.
//     */
//    private void displayInteractables(QuerySnapshot snapshot, InteractableLoadCallback callback) {
//        for (QueryDocumentSnapshot document : snapshot) {
//            boolean add = list.add(new I(snapshot));
//            I.cre(document, new InteractableCreateCallback<I>() {
//                @Override
//                public void onInteractableCreated(I interactable) {
//                    list.add(interactable);
//                    adapter.notifyDataSetChanged();
//                    callback.OnLoaded();
//                }
//            });
//        }
//    }
//
//    public Post get(int position) {
//        if (position < 0 || position >= list.size()) {
//            return null;
//        }
//
//        return list.get(position);
//    }
//
//    public CollectionReference getCollection() {
//        return FirebaseFirestore.getInstance().collection("posts");
//    }
//
//    public int size() {
//        return list.size();
//    }
//}
