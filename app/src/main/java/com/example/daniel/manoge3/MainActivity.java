package com.example.daniel.manoge3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import helper.DatabaseHelper;
import model.Weight;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private CompactCalendarView mCalendarView;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalendarView = findViewById(R.id.mainCalendar);
        mCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        mCalendarView.displayOtherMonthDays(false);
        mCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        mCalendarView.shouldSelectFirstDayOfMonthOnScroll(false);
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
                inputActivityFabIntent.putExtra("requestedTabPage", 2);
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
        //
        // Calendar
        //
        //mCalendarView = findViewById(R.id.mainCalendar);
        //mCalendarView.setDate(Calendar.getInstance().getTimeInMillis(), false, true);
        /*mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String stringDate = (i1 + 1) + "/" + i2 + "/" + i;
                String dateArray[] = stringDate.split("/");
                String zeroDateArray[] = {
                        String.format("%02d", Integer.valueOf(dateArray[0])),
                        String.format("%02d", Integer.valueOf(dateArray[1])),
                        dateArray[2]
                };
                stringDate = zeroDateArray[0] + "/" + zeroDateArray[1] + "/" + zeroDateArray[2];
                Log.d(TAG, "onSelectedDayChange: stringDate: " + stringDate);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                Date date = null;
                try {
                    date = sdf.parse(stringDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long integerDate = date.getTime();
                Log.d(TAG, "onSelectedDayChange: integerDate: " + integerDate);
                // --
                Intent inputIntent = new Intent(MainActivity.this, InputActivity.class);
                inputIntent.putExtra("stringDate", stringDate);
                inputIntent.putExtra("integerDate", integerDate);
                // --
                startActivity(inputIntent);
            }
        });*/

        //dbh.closeDB();
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

        db = DatabaseHelper.getInstance(this);

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
}
