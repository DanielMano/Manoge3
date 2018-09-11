package com.example.daniel.manoge3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import helper.DatabaseHelper;
import model.DayExercise;

/**
 * Created by Daniel on 8/27/2018.
 */

public class InputActivityCustomAdapter extends ArrayAdapter<DayExercise> {

    private static final String TAG = "InputActivityCustomAdap";

    private Context mContext;

    public InputActivityCustomAdapter(@NonNull Context mContext, int resource, @NonNull ArrayList<DayExercise> objects) {
        super(mContext, resource, objects);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_input_listview_item, null);
        }

        final DayExercise item = getItem(position);

        if (item != null) {
            TextView tvName = v.findViewById(R.id.act_in_lv_exerciseName_tv);
            TextView tvSetString = v.findViewById(R.id.act_in_lv_setsString_tv);
            Button menuButton = v.findViewById(R.id.act_in_editButton);


            if (tvName != null) {
                tvName.setText(item.getName());
            }

            if (tvSetString != null)
                tvSetString.setText(item.getSets());

            if (menuButton != null){
                menuButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        PopupMenu popup = new PopupMenu(getContext(), view);
                        popup.getMenuInflater().inflate(R.menu.activity_input_listview_menu,
                                popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.act_in_lv_menu1:
                                        Log.d(TAG, "onMenuItemClick: EDIT");
                                        ((InputActivity)mContext).editSets(position);
                                        break;
                                    case R.id.act_in_lv_menu2:
                                        ((InputActivity)mContext).deleteFromList(position);
                                        break;
                                    default:
                                        break;
                                }
                                return true;
                            }
                        });
                    }
                });
            }
        }
        return v;
    }
}
