package com.example.daniel.manoge3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import model.Exercise;

/**
 * Created by Daniel on 9/4/2018.
 */

public class InputItemTab2RoutineCustomAdapter extends ArrayAdapter<Exercise> {
    private static final String TAG = "InputItemTab3RoutineCus";

    private Context mContext;

    public InputItemTab2RoutineCustomAdapter(@NonNull Context mContext, int resource, @NonNull List<Exercise> objects) {
        super(mContext, resource, objects);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.exercise_listview_name_item, null);
        }

        final Exercise item = getItem(position);

        if (item != null) {
            TextView tvName = v.findViewById(R.id.ex_lv_name);

            if (tvName != null) {
                tvName.setText(item.getName());
            }
        }
        return v;
    }
}
