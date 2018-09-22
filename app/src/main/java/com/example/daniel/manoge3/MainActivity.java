package com.example.daniel.manoge3;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import helper.DatabaseHelper;
import model.Exercise;
import model.Rep;
import model.Weight;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private CompactCalendarView mCalendarView;
    private static MainActivity instance;

    private TextView monthYear;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());


    DatabaseHelper db;

    public static MainActivity getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        db = DatabaseHelper.getInstance(this);
        
        mCalendarView = findViewById(R.id.mainCalendar);
        mCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        mCalendarView.displayOtherMonthDays(false);
        mCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        mCalendarView.shouldSelectFirstDayOfMonthOnScroll(false);

        // Get events for weights and workouts from database in parallel in the background
        new PopulateWeightEvents().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new PopulateWorkoutEvents().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        monthYear = findViewById(R.id.month_year_tv);
        monthYear.setText(dateFormatForMonth.format(mCalendarView.getFirstDayOfCurrentMonth()));

        mCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                long integerDate = dateClicked.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                sdf.setTimeZone(TimeZone.getDefault());
                String stringDate = sdf.format(dateClicked);


                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra("stringDate", stringDate);
                intent.putExtra("integerDate", integerDate);
                startActivity(intent);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                monthYear.setText(dateFormatForMonth.format(mCalendarView.getFirstDayOfCurrentMonth()));
            }
        });

        //
        // Fab buttons
        //
        final FloatingActionButton weightGraphFab = findViewById(R.id.weightGraphFab);
        weightGraphFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent weightGraphIntent = new Intent(MainActivity.this, WeightGraph.class);
                startActivity(weightGraphIntent);
            }
        });

        FloatingActionButton databaseFab = findViewById(R.id.databaseFab);
        databaseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent databaseFabIntent = new Intent(MainActivity.this, DatabaseActivity.class);
                startActivity(databaseFabIntent);
            }
        });

        FloatingActionButton inputActivityFab= findViewById(R.id.inputActivityFab);
        inputActivityFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inputActivityFabIntent = new Intent(MainActivity.this, InputItemActivity.class);
                inputActivityFabIntent.putExtra("requestedTabPage", 1);
                startActivity(inputActivityFabIntent);
            }
        });

        FloatingActionButton startWorkoutFab = findViewById(R.id.startWorkoutFab);
        startWorkoutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startWorkoutFabIntent = new Intent(MainActivity.this, BeginWorkout.class);
                startActivity(startWorkoutFabIntent);
            }
        });

        final FloatingActionButton yearLeftFab = findViewById(R.id.yearLeftFab);
        yearLeftFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mCalendarView.getFirstDayOfCurrentMonth());
                calendar.add(Calendar.YEAR, -1);
                mCalendarView.setCurrentDate(calendar.getTime());
                monthYear.setText(dateFormatForMonth.format(mCalendarView.getFirstDayOfCurrentMonth()));
            }
        });

        final FloatingActionButton monthLeftFab = findViewById(R.id.monthLeftFab);
        monthLeftFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollLeft();
            }
        });

        final FloatingActionButton yearRightFab = findViewById(R.id.yearRightFab);
        yearRightFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mCalendarView.getFirstDayOfCurrentMonth());
                calendar.add(Calendar.YEAR, 1);
                mCalendarView.setCurrentDate(calendar.getTime());
                monthYear.setText(dateFormatForMonth.format(mCalendarView.getFirstDayOfCurrentMonth()));
            }
        });

        final FloatingActionButton monthRightFab = findViewById(R.id.monthRightFab);
        monthRightFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollRight();
            }
        });
    }

    public void populateWeightTable(){

        // Current as of 5/20/2018

        String[] dates = {
                "02/06/2018",
                "02/07/2018",
                "02/08/2018",
                "02/21/2018",
                "02/23/2018",
                "02/28/2018",
                "03/02/2018",
                "03/05/2018",
                "03/07/2018",
                "03/12/2018",
                "03/14/2018",
                "03/19/2018",
                "03/23/2018",
                "04/04/2018",
                "04/05/2018",
                "04/11/2018",
                "04/12/2018",
                "04/16/2018",
                "04/18/2018",
                "04/23/2018",
                "04/24/2018",
                "04/25/2018",
                "04/29/2018",
                "05/01/2018",
                "05/03/2018",
                "05/06/2018",
                "05/07/2018",
                "05/08/2018",
                "05/12/2018"
        };

        float[] weights = {
                114.0f,
                113.6f,
                113.8f,
                114.6f,
                115.3f,
                115.0f,
                115.2f,
                115.0f,
                115.8f,
                116.0f,
                116.0f,
                116.4f,
                115.6f,
                115.3f,
                117.8f,
                117.8f,
                117.8f,
                117.8f,
                117.8f,
                117.8f,
                117.8f,
                117.8f,
                117.8f,
                117.8f,
                117.8f,
                117.8f,
                117.6f,
                117.4f,
                116.4f
        };

        long[] longDates = new long[dates.length];

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        Date date = null;

        for (int i = 0; i < dates.length; i++){
            try {
                date = sdf.parse(dates[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long integerDate = date.getTime();
            longDates[i] = integerDate;

            Weight w = new Weight();
            w.setID(i);
            Log.d(TAG, "w id: " + w.getID());
            w.setDate(longDates[i]);
            Log.d(TAG, "w date: " + w.getDate());
            w.setBodyWeight(weights[i]);
            Log.d(TAG, "w weight: " + w.getBodyWeight());
            Log.d(TAG, "w being created: " + w.toString());

            db.createWeight(new Weight(i, longDates[i], weights[i]));
        }

        Log.d(TAG, "dates: " + Arrays.toString(dates));
        Log.d(TAG, "longDates: " + Arrays.toString(longDates));
        Log.d(TAG, "weights: " + Arrays.toString(weights));


    }
    public void populateExerciseTable(){
        String[] names = {
                "Barbell Squat",
                "Barbell Military Press",
                "Hamstring Curl",
                "Wide Grip Lat Pulldown",
                "Dip",
                "Pull Ups",
                "Dumbbell Standing One Arm Bicep Curl",
                "Cable Rope Triceps Pushdown",
                "Seated Cable Row",
                "Barbell Incline Bench Press",
                "Barbell Lying Triceps Extension",
                "EZ Bar Curl",
                "Cable Triceps Pushdown",
                "Barbell Bent Over Row",
                "Barbell Bench Press",
                "Dumbbell Hammer Curls",
                "Barbell Deadlift",
                "Cable Straight Arm Pushdown",
                "Dumbbell Lateral Raise",
                "Dumbbell Incline Bench Press",
                "Barbell Rack Pull",
                "T Bar Lying Row",
                "Dumbbell Bicep Curl",
                "Cable One Arm High Curl",
                "Dumbbell Incline Curl",
                "Barbell Shrug"
        };

        float[] defaultWeights = {
                45.0f,
                45.0f,
                0f,
                0f,
                0f,
                0f,
                0f,
                0f,
                0f,
                45.0f,
                45.0f,
                25.0f,
                0f,
                45.0f,
                45.0f,
                0f,
                45.0f,
                0f,
                0f,
                0f,
                45.0f,
                0f,
                0f,
                0f,
                0f,
                45.0f
        };

        for (int i = 0; i < names.length; i++){
            Exercise ex = new Exercise();
            ex.setID(i);
            ex.setName(names[i]);
            ex.setBarWeight(defaultWeights[i]);
            db.createExercise(ex);
        }
    }

    public void addWeightEvent(long dateInMillis){
        Event weightEvent = new Event(Color.RED, dateInMillis);
        mCalendarView.addEvent(weightEvent);
        Log.d(TAG, "addWeightEvent: weightEvent added: " + weightEvent);
    }

    public void deleteWeightEvent(long dateInMillis){
        for (Event e : mCalendarView.getEvents(dateInMillis)){
            // Weight events are colored RED
            if (e.getColor() == Color.RED) {
                mCalendarView.removeEvent(e);
                Log.d(TAG, "deleteWeightEvent: weightEvent removed: " + e);
            }
        }
    }

    public void addWorkoutEvent(long dateInMillis){
        Event workoutEvent = new Event(Color.BLUE, dateInMillis);
        mCalendarView.addEvent(workoutEvent);
        Log.d(TAG, "addWorkoutEvent: workoutEvent added: " + workoutEvent);
    }

    public void deleteWorkoutEvent(long dateInMillis){
        for (Event e : mCalendarView.getEvents(dateInMillis)){
            // Workout events are colored BLUE
            if (e.getColor() == Color.BLUE) {
                mCalendarView.removeEvent(e);
                Log.d(TAG, "deleteWorkoutEvent: workoutEvent removed: " + e);
            }
        }
    }

    public boolean workoutEventExists(long dateInMillis){
        for (Event e : mCalendarView.getEvents(dateInMillis)){
            // Workout events are colored BLUE
            if (e.getColor() == Color.BLUE) {
                return true;
            }
        }
        return false;
    }


    private class PopulateWeightEvents extends AsyncTask<Void, Void, List<Weight>> {
        DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());

        @Override
        protected List<Weight> doInBackground(Void... voids) {
            if (db.getAllWeights() != null){
                return db.getAllWeights();
            } else
                return null;
        }

        @Override
        protected void onPostExecute(List<Weight> weights) {
            for (Weight weight : weights){
                Event weightEvent = new Event(Color.RED, weight.getDate());
                mCalendarView.addEvent(weightEvent);
            }
            Log.d(TAG, "onPostExecute: weightEvents added");
        }
    }

    private class PopulateWorkoutEvents extends AsyncTask<Void, Void, List<Rep>> {
        DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());

        @Override
        protected List<Rep> doInBackground(Void... voids) {
            if (db.getAllReps() != null)
                return db.getAllReps();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Rep> reps) {
            long dateInMillis = 0;
            for (Rep rep : reps){
                if (rep.getDate() != dateInMillis){
                    Event workoutEvent = new Event(Color.BLUE, rep.getDate());
                    mCalendarView.addEvent(workoutEvent);
                    dateInMillis = rep.getDate();
                }
            }
            Log.d(TAG, "onPostExecute: workoutEvents added");
        }
    }
}
