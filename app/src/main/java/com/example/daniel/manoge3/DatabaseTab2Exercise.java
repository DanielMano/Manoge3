package com.example.daniel.manoge3;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import helper.DatabaseHelper;
import model.Exercise;

/**
 * Created by Daniel on 4/20/2018.
 */

public class DatabaseTab2Exercise extends Fragment{

    DatabaseHelper dbh;
    ListView exerciseListView;

    public BeginWorkoutExerciseCustomAdapter adapter;
    List<Exercise> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.begin_workout_tab2_exercise, container, false);

        dbh = DatabaseHelper.getInstance(getActivity());

        exerciseListView = view.findViewById(R.id.exerciseListView);

        list = dbh.getAllExercises();

        ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        exerciseListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
    }

    public void populateList(){
        list = dbh.getAllExercises();

        adapter = new BeginWorkoutExerciseCustomAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        exerciseListView.setAdapter(adapter);
    }
}
