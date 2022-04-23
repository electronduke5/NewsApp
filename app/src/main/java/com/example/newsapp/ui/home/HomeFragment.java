package com.example.newsapp.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.os.health.PidHealthStats;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.DatabaseHelper;
import com.example.newsapp.NewsDBContract;
import com.example.newsapp.NewsModel;
import com.example.newsapp.RecyclerViewAdapterNews;
import com.example.newsapp.databinding.FragmentNewsBinding;
import com.example.newsapp.databinding.FragmentNewsBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<NewsModel> models = new ArrayList<>();
    private FragmentNewsBinding binding;
    private DatabaseHelper databaseHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentNewsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        databaseHelper = new DatabaseHelper(getContext());

        updateRecyclerView();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateRecyclerView() {
        try (Cursor cursor = databaseHelper.getNewsData();) {
            int idColumnIndex = cursor.getColumnIndex(NewsDBContract.News._ID);
            int headerColumnIndex = cursor.getColumnIndex(NewsDBContract.News.COLUMN_TITLE);
            int mainTextColumnIndex = cursor.getColumnIndex(NewsDBContract.News.COLUMN_TEXT);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentHeader = cursor.getString(headerColumnIndex);
                String currentText = cursor.getString(mainTextColumnIndex);

                models.add(new NewsModel(currentID, currentHeader, currentText));
            }
        }

        for (NewsModel model : models) {
            Log.d("Data", model.getID() + " - " + model.getHeader());

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            RecyclerViewAdapterNews adapter = new RecyclerViewAdapterNews(getContext(), models);
            recyclerView.setAdapter(adapter);
        }
    }
}