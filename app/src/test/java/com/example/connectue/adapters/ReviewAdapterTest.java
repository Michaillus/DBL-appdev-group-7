package com.example.connectue.adapters;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.example.connectue.model.Review;
import com.example.connectue.model.StudyUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ReviewAdapterTest {

    @Mock
    List<Review> mockReviewList;
    @Mock
    StudyUnit mockStudyUnit;
    private ReviewAdapter reviewAdapter;
    @Mock
    View mockView;
    @Mock
    LayoutInflater mockLayoutInflater;

    @Mock
    FragmentManager fragmentManager;

    @Test
    public void testGetItemCount() {
        // Prepare test data
        List<Review> reviewList = new ArrayList<>();


        // Instantiate ReviewAdapter with the test data
        ReviewAdapter reviewAdapter = new ReviewAdapter(reviewList, fragmentManager, mockStudyUnit);

        // Invoke getItemCount() method
        int itemCount = reviewAdapter.getItemCount();

        // Assert the result
        assertEquals(reviewList.size(), itemCount);
    }


}

