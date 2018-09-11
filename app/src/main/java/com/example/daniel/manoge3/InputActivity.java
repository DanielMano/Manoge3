package com.example.daniel.manoge3;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import helper.DatabaseHelper;
import model.DayExercise;
import model.Exercise;
import model.Rep;
import model.Weight;

public class InputActivity extends AppCompatActivity {

    private static final String TAG = "InputActivity";

    private InputActivityCustomAdapter adapter;
    ArrayList<DayExercise> list = new ArrayList<>();
    List<String> repsOnDayList = new ArrayList<>();

    String selectedDate;
    public static long selectedIntegerDate;
    List<String> exerciseNames = new ArrayList<>();
    List<Integer> exerciseIDs = new ArrayList<>();

    TextView inputDate;
    EditText inputWeight;
    ListView inputRepListView;

    DatabaseHelper dbh;

    private boolean weightExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        dbh = DatabaseHelper.getInstance(this);

        // input date for database
        Intent receivedIntent = getIntent();
        selectedIntegerDate = receivedIntent.getLongExtra("integerDate", 0);
        // input date for text field
        inputDate = findViewById(R.id.inputDateTextView);
        selectedDate = receivedIntent.getStringExtra("stringDate");
        inputDate.setText(selectedDate);

        // weight field
        inputWeight = findViewById(R.id.weightInputEditText);

        // if date already in database, set hint to previous weight
        Cursor cursor = dbh.getDate(selectedIntegerDate);
        if (cursor != null && cursor.moveToFirst()){
            inputWeight.setHint(cursor.getString(2));
            weightExists = true;
            cursor.close();
        }

        // ListView of reps
        inputRepListView = findViewById(R.id.inputRepListView);
        //populateModelList();


        // enters or update date, weight pair into database
        FloatingActionButton saveInputFab = (FloatingActionButton) findViewById(R.id.saveInputFab);
        saveInputFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sWeight = inputWeight.getText().toString();
                if (sWeight.length() == 0) {
                    toastMessage("You must enter a weight.");
                } else {
                    float weight = Float.valueOf(sWeight);
                    int result = Float.compare(weight, 00.00f);
                    if (result <= 0) {
                        inputWeight.setText("");
                        toastMessage("Weight entered must be greater than 0.");
                    } else {
                        Weight newWeight = new Weight();
                        newWeight.setDate(selectedIntegerDate);
                        newWeight.setBodyWeight(weight);
                        Log.d(TAG, "onClick: " + newWeight.getID() + ", " + newWeight.getDate() + ", " + newWeight.getBodyWeight());
                        if (weightExists) {
                            dbh.updateWeight(newWeight);
                            toastMessage("Weight Updated");
                            Log.d(TAG, "updateWeight: " + selectedDate + ", " + weight);
                            finish();
                        }
                        else {
                            dbh.createWeight(newWeight);
                            toastMessage("Weight Created");
                            Log.d(TAG, "createWeight: " + selectedDate + ", " + weight);
                            finish();
                        }
                    }
                }
            }
        });

        // adds an exercise to specific day
        FloatingActionButton addExerciseFab = findViewById(R.id.addExerciseFab);
        addExerciseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InputActivity.this, BeginWorkout.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromAddExercise", true);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateModelList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        populateModelList();
    }

    public void populateModelList() {
        repsOnDayList = dbh.joinRepExerciseOnDate(selectedIntegerDate);

        String defaultName = "";
        int count = 1;
        String print = "";

        list.clear();
        exerciseIDs.clear();
        exerciseNames.clear();

        for (String element : repsOnDayList) {
            // splitRep[ string name, float bar, float plate, int numreps ]
            String splitRep[] = element.split("-");
            String name = splitRep[0];
            float totalWeight = Float.parseFloat(splitRep[1]) + (Float.parseFloat(splitRep[2]) * 2);
            int numreps = Integer.valueOf(splitRep[3]);

            if (!name.equals(defaultName)) {
                if (!defaultName.equals("")) {
                    DayExercise item = new DayExercise(defaultName, print);
                    list.add(item);
                }
                count = 1;
                print = "";
                print += ("Set " + count + ": " + totalWeight + " x " + numreps);
                count++;
                defaultName = name;
                Log.d(TAG, "populateModelList: defaultName: " + defaultName);
                exerciseNames.add(String.valueOf(defaultName));
            } else {
                print += ("\nSet " + count + ": " + totalWeight + " x " + numreps);
                count++;
            }
        }
        Log.d(TAG, "populateModelList: exerciseNames: " + exerciseNames.toString());
        DayExercise item = new DayExercise(defaultName, print);

        if (!item.getName().contentEquals(""))
            list.add(item);

        for (String name : exerciseNames) {
            Exercise ex = dbh.getExercise(name);
            int ID = (int) ex.getID();
            exerciseIDs.add(ID);
        }

        if(adapter == null){
            InputActivityCustomAdapter adapter =
                    new InputActivityCustomAdapter(this,
                            R.layout.activity_input_listview_item,
                            list);
            inputRepListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public void editSets(int position) {
        toastMessage("Editing Exercise");
        Log.d(TAG, "editSets: date: " + selectedIntegerDate + ", eID: " + exerciseIDs.get(position) + ", name: " + exerciseNames.get(position));
        ArrayList<Rep> repsToEdit = dbh.getRepsOnDateAndID(selectedIntegerDate, exerciseIDs.get(position));
        Log.d(TAG, "editSets: list: " + repsToEdit.toString());


        Intent intent = new Intent(InputActivity.this, InputItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("repsToEditList", repsToEdit);
        bundle.putInt("requestedTabPage", 0);
        bundle.putString("exerciseName", exerciseNames.get(position));
        bundle.putBoolean("fromEdit", true);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void deleteFromList(int position){
        toastMessage("Deleting Exercise");
        dbh.deleteRepsWithIDOnDate(selectedIntegerDate, exerciseIDs.get(position));
        Log.d(TAG, "deleteFromList: " + selectedIntegerDate + ", " + exerciseIDs.get(position));
        onResume();
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
