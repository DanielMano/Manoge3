package com.example.daniel.manoge3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import model.Exercise;

/**
 * Created by Daniel on 9/1/2018.
 */

public class BeginWorkoutExerciseCustomAdapter extends ArrayAdapter<Exercise> {

    private List<Exercise> dataSet;
    Context mContext;

    public BeginWorkoutExerciseCustomAdapter(@NonNull Context mContext, int resource, List<Exercise> dataSet) {
        super(mContext, resource, dataSet);
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(mContext);
            v = vi.inflate(R.layout.begin_workout_exercise_listview_item, null);
        }

        Exercise item = getItem(position);

        if (item != null) {
            TextView name = v.findViewById(R.id.bwe_lv_exercise_name);
            TextView weight = v.findViewById(R.id.bwe_lv_bar_weight);

            if (name != null)
                name.setText(item.getName());
            if (weight != null) {
                if (item.getBarWeight() != 0.0f)
                    weight.setText(String.valueOf(item.getBarWeight()) + " lbs.");
                else {
                    weight.setText("");
                    weight.setHint("");
                }
            }
        }
        return v;
    }

    public Exercise getExerciseAtPosition(int position){
        return dataSet.get(position);
    }
}
