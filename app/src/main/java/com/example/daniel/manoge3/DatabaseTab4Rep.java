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
import model.Exercise;
import model.Rep;

/**
 * Created by Daniel on 4/20/2018.
 */

public class DatabaseTab4Rep extends Fragment{

    DatabaseHelper dbh;
    ListView repListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.database_tab4_reps, container, false);

        dbh = DatabaseHelper.getInstance(getActivity());

        repListView = view.findViewById(R.id.repListView);

        List<Rep> list = dbh.getAllReps();

        ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        repListView.setAdapter(adapter);

        return view;
    }
}
