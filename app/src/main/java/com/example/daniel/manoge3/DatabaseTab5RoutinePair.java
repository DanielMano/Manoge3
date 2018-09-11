package com.example.daniel.manoge3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import helper.DatabaseHelper;
import model.Rep;
import model.RoutineExercise;

/**
 * Created by Daniel on 7/1/2018.
 */

public class DatabaseTab5RoutinePair extends Fragment {

    DatabaseHelper dbh;
    ListView routinePairsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.database_tab5_routine_pairs, container, false);

        dbh = DatabaseHelper.getInstance(getActivity());

        routinePairsListView = view.findViewById(R.id.routinePairsListView);

        List<RoutineExercise> list = dbh.getAllPairs();

        ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        routinePairsListView.setAdapter(adapter);

        return view;
    }
}
