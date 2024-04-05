package com.example.connectue.adapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.example.connectue.R;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.Material;
import com.example.connectue.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MaterialAdapterTest {
    @Mock
    FragmentManager fragmentManager;

    @Test
    public void testGetItemCount() {
        // Prepare test data
        List<Material> materialList = new ArrayList<>();

        // Instantiate MaterialAdapter with the test data
        MaterialAdapter materialAdapter = new MaterialAdapter(materialList, fragmentManager);

        // Invoke getItemCount() method
        int itemCount = materialAdapter.getItemCount();

        // Assert the result
        assertEquals(materialList.size(), itemCount);
    }
}
