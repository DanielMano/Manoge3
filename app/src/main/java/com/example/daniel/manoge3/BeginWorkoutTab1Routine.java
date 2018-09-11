package com.example.daniel.manoge3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

import helper.DatabaseHelper;
import model.RoutineExercise;

/**
 * Created by Daniel on 5/10/2018.
 */

public class BeginWorkoutTab1Routine extends Fragment {
    private static final String TAG = "BeginWorkoutTab1Routine";

    private ArrayList<RoutineExercise> mPairs;
    private ArrayList<String> mNames;
    private ArrayList<Integer> mCounts;
    private ArrayList<Integer> mRoutineIDs;

    RecyclerViewAdapter recyclerViewAdapter;

    private Bundle bundleToPass;

    DatabaseHelper dbh;

    @Override
    public void onResume() {
        super.onResume();
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbh = DatabaseHelper.getInstance(getActivity());

        mPairs = dbh.getAllPairs();
        mNames = new ArrayList<>();
        mCounts = new ArrayList<>();
        mRoutineIDs = new ArrayList<>();

        boolean nameInList = false;

        int routineID;
        if (mPairs.size() != 0) {
            for (int i = 0; i < mPairs.size(); i++) {
                Log.d(TAG, "pair " + i + " = " + mPairs.get(i));
                routineID = mPairs.get(i).getRoutineID();
                Log.d(TAG, "id: " + routineID);

                String name = dbh.getRoutine(routineID);
                if (mNames.contains(name)) {
                    Log.d(TAG, "name in list - continue");
                    continue;
                }
                Log.d(TAG, "name NOT in list");
                int count = dbh.getExerciseCount(routineID);

                Log.d(TAG, "enter do loop");
                mNames.add(name);
                mCounts.add(count);
                mRoutineIDs.add(routineID);
                Log.d(TAG, "added: " + mNames + ", " + mCounts);
                Log.d(TAG, "mNames contains name: " + mNames.contains(name));
            }
        }

        //get Bundle if possible
        bundleToPass = getActivity().getIntent().getExtras();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.begin_workout_tab1_routines, container, false);

        RecyclerView myRecyclerView = view.findViewById(R.id.workoutRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), mNames, mCounts, mRoutineIDs, bundleToPass);
        myRecyclerView.setAdapter(recyclerViewAdapter);

        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}
