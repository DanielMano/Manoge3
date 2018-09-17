package com.example.daniel.manoge3;

import android.content.Intent;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;

import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import helper.DatabaseHelper;
import model.Exercise;
import model.Routine;

/**
 * Created by Daniel on 4/20/2018.
 */

public class RoutineExercisesActivity extends AppCompatActivity{

    private static final String TAG = "RoutineExercisesActivit";

    DatabaseHelper dbh;
    ListView routineExerciseListView;
    TextView routineName;
    FloatingActionButton editRoutineFab;
    FloatingActionButton deleteRoutineFab;
    List<Exercise> list;
    public BeginWorkoutExerciseCustomAdapter adapter;

    int routineID;
    boolean fromAddExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_exercises);

        dbh = DatabaseHelper.getInstance(this);

        editRoutineFab = findViewById(R.id.editRoutineFab);
        deleteRoutineFab = findViewById(R.id.deleteRoutineFab);
        routineExerciseListView = findViewById(R.id.routineExerciseListView);
        routineName = findViewById(R.id.routineName);

        fromAddExercise = false;
        getIncomingIntent();
        Log.d(TAG, "exercise from routine# - " + routineID);

        routineExerciseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                long exerciseID = list.get(i).getID();
                String exerciseName = list.get(i).getName();
                float exerciseDefaultWeight = list.get(i).getBarWeight();

                Intent intent = new Intent(RoutineExercisesActivity.this, InputSetActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("beginWorkoutExerciseName", exerciseName);
                bundle.putFloat("beginWorkoutExerciseDefaultWeight", exerciseDefaultWeight);
                bundle.putLong("beginWorkoutExerciseID", exerciseID);

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
                Log.d(TAG, "onItemClick: todayDate: " + todayDate);
                Log.d(TAG, "onItemClick: fromAddExercise: " + fromAddExercise + ", therefore use todays date: " + !fromAddExercise);

                // If not coming from InputActivity addExercise, use today's date
                if (!fromAddExercise) {
                    bundle.putLong("longDate", todayDate);
                    Log.d(TAG, "onItemClick: using defaultDate: " + todayDate);
                }
                // else use date from selected day
                else {
                    bundle.putLong("longDate", InputActivity.selectedIntegerDate);
                    Log.d(TAG, "onItemClick: using selectedDate: " + InputActivity.selectedIntegerDate);
                }

                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        editRoutineFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Routine routineToEdit = dbh.getRoutine(routineName.getText().toString());
                Intent intent = new Intent(RoutineExercisesActivity.this, InputItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("routineToEdit", routineToEdit);
                intent.putExtras(bundle);
                startActivity(intent);
                Log.d(TAG, "onClick: editing routine: " + routineToEdit);
                toastMessage("Editing " + routineToEdit.getName() + " Routine");
            }
        });

        deleteRoutineFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Routine routine = dbh.getRoutine(routineName.getText().toString());
                dbh.deleteRoutine(routine.getID());
                dbh.deletePairs(routine);
                Log.d(TAG, "onClick: deleting routine: " + routine.getName());
                toastMessage("Routine " + routine.getName() + " Deleted");
                finish();
            }
        });
    }

    private void getIncomingIntent(){

        Bundle receivedBundle = this.getIntent().getExtras();
        Log.d(TAG, "getIncomingIntent: " + receivedBundle.getString("routineName") + ", " + receivedBundle.getInt("routineID"));
        if (!receivedBundle.getString("routineName").equals(null)) {
            routineName.setText(receivedBundle.getString("routineName"));
            routineID = receivedBundle.getInt("routineID", 0);
            fromAddExercise = receivedBundle.getBoolean("fromAddExercise", false);
            Log.d(TAG, "getIncomingIntent: fromAddExercise: " + fromAddExercise);
        }
    }

    public void populateList(){
        list = dbh.getExercisesFromRoutine(routineID);

        adapter = new BeginWorkoutExerciseCustomAdapter(this,
                R.layout.begin_workout_exercise_listview_item,
                list);
        routineExerciseListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateList();
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
