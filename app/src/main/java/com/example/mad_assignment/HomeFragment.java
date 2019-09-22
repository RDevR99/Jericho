package com.example.mad_assignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<LectureDetails> lectureDetailsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, null);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        lectureDetailsList = new ArrayList<>();

        //DUMMY DATA
        for(int i=0; i<=10; i++)
        {
            LectureDetails lectureDetail = new LectureDetails(
                    "CSE"+(i+1)+"MAD",
                    "GS"
            );

            lectureDetailsList.add(lectureDetail);
        }

        adapter = new LectureDetailsAdapter(lectureDetailsList, getContext());

        recyclerView.setAdapter(adapter);
    }
}
