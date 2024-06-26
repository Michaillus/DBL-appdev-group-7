package com.example.connectue.adapters;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.R;
import com.example.connectue.databinding.RowPostsBinding;
import com.example.connectue.model.Post;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PostAdapterTest {

    // Mock objects which postAdapter depends on
    @Mock
    ViewGroup mockParent;
    @Mock
    LayoutInflater mockLayoutInflater;

    List<Post> postList;

    String currentUid;

    FragmentManager fragmentManager;

    PostAdapter postAdapter;

    /**
     * Initialize parameters to be tested with
     */
    @Before
    public void setUp() {
        // Create a postList with 3 mock posts
        postList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String num = String.valueOf(i);
            postList.add(new Post(num, num, ""));
        }

        currentUid = "1";

        fragmentManager = Mockito.mock(FragmentManager.class);

        postAdapter = new PostAdapter(postList, fragmentManager);
    }

    /**
     * Test the constructor of PostAdapter
     */
    @Test
    public void constructorTest() {
        PostAdapter testPostAdapter = new PostAdapter(postList, fragmentManager);
        assertEquals(postAdapter.getPostList(), testPostAdapter.getPostList());
        assertEquals(postAdapter.getFragmentManager(), testPostAdapter.getFragmentManager());
    }

    /**
     * Test the getters of PostAdapter
     */
    @Test
    public void gettersTest() {
        assertEquals(postAdapter.getPostList(), postList);
        assertEquals(postAdapter.getFragmentManager(), fragmentManager);
    }

    /**
     * Test the onCreateViewHolder method which create connection with the post holder
     */
    @Test
    public void onCreateViewHolder() {
//        PostAdapter spyAdapter = Mockito.spy(postAdapter);
////
////        mockParent = Mockito.mock(ViewGroup.class);
////        mockLayoutInflater = Mockito.mock(LayoutInflater.class);
//        doReturn(mockLayoutInflater).when(spyAdapter).inflateView(mockParent);
//        int viewType = 1;
//        //LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//        //RowPostsBinding binding = RowPostsBinding.inflate(layoutInflater, parent, false);
//        PostAdapter.MyHolder holder = spyAdapter.onCreateViewHolder(mockParent, viewType);
//        //assertEquals(holder.getBinding(), binding);
//        assertNotNull(holder);
    }

    /**
     * Test the onBindViewHolder which creates binding with the post holder.
     */
    @Test
    public void onBindViewHolder() {
    }

    /**
     * test the getItemCount method which return the number of elements in the postList.
     */
    @Test
    public void getItemCount() {
        assertEquals(postAdapter.getItemCount(), 3);
    }
}