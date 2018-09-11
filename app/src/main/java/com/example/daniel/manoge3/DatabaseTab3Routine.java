package com.example.daniel.manoge3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import helper.DatabaseHelper;
import model.Routine;

/**
 * Created by Daniel on 4/20/2018.
 */

public class DatabaseTab3Routine extends Fragment{
    private static final String TAG = "Tab3DatabaseDaysExercis";

    DatabaseHelper dbh;
    ListView routineListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.database_tab3_routine_names, container, false);

        dbh = DatabaseHelper.getInstance(getActivity());

        routineListView = view.findViewById(R.id.routineListView);

        final List<Routine> list = dbh.getAllRoutines();

        ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        routineListView.setAdapter(adapter);

        routineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //dbh.deleteAllRoutines();
                Log.d(TAG, "onItemClick: routine " + list.get(i).getID() + " clicked.");
            }
        });

        return view;
    }
}
