package com.example.daniel.manoge3;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import helper.DatabaseHelper;
import model.Weight;

/**
 * Created by Daniel on 4/20/2018.
 */

public class DatabaseTab1Weights extends Fragment{

    private static final String TAG = "DatabaseTab1Weights";

    private ListView weightListView;
    DatabaseHelper dbh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.database_tab1_weights, container, false);

        dbh = DatabaseHelper.getInstance(getActivity());

        weightListView = view.findViewById(R.id.weightListView);

        List<Weight> list = dbh.getAllWeights();

        ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        weightListView.setAdapter(adapter);

        // TEMP METHOD - deletes date from database onClick
        weightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                String array[] = name.split("-");
                Log.d(TAG, "onItemClick: " + array[0]);

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                Date date = null;
                try {
                    date = sdf.parse(array[0]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long integerDate = date.getTime();
                Log.d(TAG, "onItemClick: " + integerDate);

                dbh.deleteWeight(integerDate);
                Log.d(TAG, "onItemClick: deleteWeight called for date " + integerDate);
            }
        });


        return view;
    }
}
