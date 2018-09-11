package com.example.daniel.manoge3;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import helper.DatabaseHelper;

public class WeightGraph extends AppCompatActivity {

    private static final String TAG = "WeightGraph";

    DatabaseHelper dbh;

    public ArrayList<Date> dateList = new ArrayList<>();
    public ArrayList<Float> weightList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_graph);
        setTitle("Weight Graph");

        dbh = DatabaseHelper.getInstance(this);
        GraphView graph = findViewById(R.id.weightGraph);

        populateLists();

        if (dateList.size() != weightList.size()) {
            toastMessage("#date != #weight");
        } else {
            Log.d(TAG, "dateList: " + dateList.toString());
            Log.d(TAG, "weightList: " + weightList.toString());
            int seriesSize = dateList.size();
            DataPoint[] dataPoints = new DataPoint[seriesSize];
            for (int i = 0; i < seriesSize; i++){
                dataPoints[i] = new DataPoint(dateList.get(i), weightList.get(i));
            }

            /*
            PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(dataPoints);

            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
            graph.getGridLabelRenderer().setNumVerticalLabels(5);
            graph.getGridLabelRenderer().setLabelsSpace(0);
            graph.getGridLabelRenderer().setHumanRounding(false);
            graph.getGridLabelRenderer().setHorizontalLabelsAngle(135);
            graph.getGridLabelRenderer().setPadding(50);

            // set y bounds
            graph.getViewport().setMinY(114.0);
            graph.getViewport().setMaxY(122.0);
            graph.getViewport().setYAxisBoundsManual(true);

            // set x bounds
            graph.getViewport().setMinY(dateList.get(0).getTime());
            graph.getViewport().setMaxX(dateList.get(seriesSize-1).getTime());
            graph.getViewport().setXAxisBoundsManual(true);

            graph.getViewport().setScrollable(true);

            graph.addSeries(series);
            */
            PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(dataPoints);
            //series.setDrawDataPoints(true);
            //series.setDataPointsRadius(10);



            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(WeightGraph.this));
            //graph.getGridLabelRenderer().setNumHorizontalLabels(seriesSize+1); // only 4 because of the space
            graph.getGridLabelRenderer().setNumVerticalLabels(5);
            graph.getGridLabelRenderer().setLabelsSpace(0);

            // set manual y bounds
            graph.getViewport().setMinY(112.0);
            graph.getViewport().setMaxY(120.0);
            graph.getViewport().setYAxisBoundsManual(true);

            // set manual x bounds to have nice steps
            graph.getViewport().setMinX(dateList.get(0).getTime());
            graph.getViewport().setMaxX(dateList.get(seriesSize-1).getTime());
            graph.getViewport().setXAxisBoundsManual(true);

            // as we use dates as labels, the human rounding to nice readable numbers
            // is not necessary
            graph.getGridLabelRenderer().setHumanRounding(false);

            graph.getGridLabelRenderer().setHorizontalLabelsAngle(135);

            graph.getGridLabelRenderer().setPadding(50);

            graph.getViewport().setScrollable(true);

            graph.addSeries(series);
        }

    }

    private void populateLists(){
        Cursor data = dbh.getWeightsForGraph();

        long dateLong;
        String date;
        float weight;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date d;

        while(data.moveToNext()){
            // date = mm/dd/yyyy
            dateLong = data.getLong(1);
            d = new Date(dateLong);
            weight = Float.valueOf(data.getString(2));
            sdf.format(d);
            dateList.add(d);

            Log.d(TAG, "date added: " + d);
            weightList.add(weight);
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
