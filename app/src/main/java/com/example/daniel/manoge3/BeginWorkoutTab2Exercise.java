package com.example.daniel.manoge3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import helper.DatabaseHelper;
import model.Exercise;

/**
 * Created by Daniel on 4/20/2018.
 */

public class BeginWorkoutTab2Exercise extends Fragment{

    private static final String TAG = "BeginWorkoutTab2Exercis";

    DatabaseHelper dbh;
    ListView exerciseListView;
    List<Exercise> list;

    boolean fromAddExercise;

    public BeginWorkoutExerciseCustomAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        populateListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.begin_workout_tab2_exercise, container, false);

        dbh = DatabaseHelper.getInstance(getActivity());

        exerciseListView = view.findViewById(R.id.exerciseListView);

        fromAddExercise = false;
        getBundleExtras();

        exerciseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                long exerciseID = list.get(i).getID();
                String exerciseName = list.get(i).getName();
                float exerciseDefaultWeight = list.get(i).getBarWeight();

                Intent intent = new Intent(getContext(), InputSetActivity.class);
                Bundle passBundle= new Bundle();
                passBundle.putString("beginWorkoutExerciseName", exerciseName);
                passBundle.putFloat("beginWorkoutExerciseDefaultWeight", exerciseDefaultWeight);
                passBundle.putLong("beginWorkoutExerciseID", exerciseID);

                // takes current day at 00:00:00:000 time for default longDate
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                // should be whatever timezone the app is in
                TimeZone tz = TimeZone.getDefault();
                today.setTimeZone(tz);
                long todayDate = today.getTimeInMillis();


                if (!fromAddExercise)
                    passBundle.putLong("longDate", todayDate);
                else
                    passBundle.putLong("longDate", InputActivity.selectedIntegerDate);

                intent.putExtras(passBundle);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflate = new MenuInflater(getActivity());
        inflate.inflate(R.menu.activity_input_listview_menu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Exercise ex = adapter.getExerciseAtPosition(info.position);

        menu.setHeaderTitle(ex.getName());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Exercise ex = adapter.getExerciseAtPosition(info.position);

        switch (item.getItemId()){
            case R.id.act_in_lv_menu1:
                toastMessage("Editing " + ex.toString());
                Log.d(TAG, "onContextItemSelected: will edit: " + ex.toString());
                editExercise(ex);
                break;
            case R.id.act_in_lv_menu2:
                toastMessage(ex.toString() + " Deleted");
                Log.d(TAG, "onContextItemSelected: will delete: " + ex.toString());
                deleteExercise(ex);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void populateListView(){
        Log.d(TAG, "populateListView: populating");
        list = dbh.getAllExercises();

        adapter = new BeginWorkoutExerciseCustomAdapter(getActivity(),
                R.layout.begin_workout_exercise_listview_item,
                list);
        exerciseListView.setAdapter(adapter);

        registerForContextMenu(exerciseListView);
    }


    public void editExercise(Exercise ex){
        Intent intent = new Intent(getActivity(), InputItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("exerciseToEdit", ex);
        bundle.putInt("requestedTabPage", 0);
        bundle.putBoolean("fromEditExercise", true);
        intent.putExtras(bundle);
        startActivity(intent);

        Log.d(TAG, "editExercise: exID: " + ex);
    }

    public void deleteExercise(Exercise ex){
        dbh.deleteExercise(ex.getName());
        Log.d(TAG, "deleteExercise: " + ex);
        onResume();
    }

    private void toastMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void getBundleExtras(){
        if (getActivity().getIntent().hasExtra("fromAddExercise")){
            fromAddExercise = getActivity().getIntent().getBooleanExtra("fromAddExercise", false);
        }
    }
}
