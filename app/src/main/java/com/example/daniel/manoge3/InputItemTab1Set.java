package com.example.daniel.manoge3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import helper.DatabaseHelper;
import model.Rep;

/**
 * Created by Daniel on 5/3/2018.
 */

public class InputItemTab1Set extends Fragment {
    private static final String TAG = "InputItemTab1Set";

    LinearLayout setListLL;
    Button addSetButton, removeSetButton, saveSetButton;
    int setCount = 3;
    TextView inputSetTitle, dateTextView;

    EditText set1DefaultWeight, set2DefaultWeight, set3DefaultWeight;
    EditText set1TotalWeight, set1PlateWeight;
    EditText set2TotalWeight, set2PlateWeight;
    EditText set3TotalWeight, set3PlateWeight;

    EditText set1RepNum, set2RepNum, set3RepNum;

    List<EditText> barTexts = new ArrayList<>(),
            plateTexts = new ArrayList<>(),
            totalTexts = new ArrayList<>(),
            repCounts = new ArrayList<>();

    long exerciseID;
    long longDate;
    String name;
    float weight;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_item_tab1_set, container, false);

        setListLL = view.findViewById(R.id.inputSetVerticalLayout);
        addSetButton = view.findViewById(R.id.inputSetAddButton);
        removeSetButton = view.findViewById(R.id.inputSetRemoveButton);
        saveSetButton = view.findViewById(R.id.saveSetButton);
        inputSetTitle = view.findViewById(R.id.inputSetTitle);

        set1DefaultWeight = view.findViewById(R.id.set1Bar);
        set1TotalWeight = view.findViewById(R.id.set1Weight);
        set1PlateWeight = view.findViewById(R.id.set1Plate);
        set2DefaultWeight = view.findViewById(R.id.set2Bar);
        set2TotalWeight = view.findViewById(R.id.set2Weight);
        set2PlateWeight = view.findViewById(R.id.set2Plate);
        set3DefaultWeight = view.findViewById(R.id.set3Bar);
        set3TotalWeight = view.findViewById(R.id.set3Weight);
        set3PlateWeight = view.findViewById(R.id.set3Plate);

        set1RepNum = view.findViewById(R.id.set1Rep);
        set2RepNum = view.findViewById(R.id.set2Rep);
        set3RepNum = view.findViewById(R.id.set3Rep);

        barTexts.add(set1DefaultWeight);
        barTexts.add(set2DefaultWeight);
        barTexts.add(set3DefaultWeight);
        plateTexts.add(set1PlateWeight);
        plateTexts.add(set2PlateWeight);
        plateTexts.add(set3PlateWeight);
        totalTexts.add(set1TotalWeight);
        totalTexts.add(set2TotalWeight);
        totalTexts.add(set3TotalWeight);
        repCounts.add(set1RepNum);
        repCounts.add(set2RepNum);
        repCounts.add(set3RepNum);

        addMyListenerToEditText(set1DefaultWeight, set1PlateWeight, set1TotalWeight);
        addMyListenerToEditText(set2DefaultWeight, set2PlateWeight, set2TotalWeight);
        addMyListenerToEditText(set3DefaultWeight, set3PlateWeight, set3TotalWeight);

        // try to get bundle
        Bundle bundle = getActivity().getIntent().getExtras();

        name = bundle.getString("beginWorkoutExerciseName");
        weight = bundle.getFloat("beginWorkoutExerciseDefaultWeight", 0);
        exerciseID = bundle.getLong("beginWorkoutExerciseID", 0);
        setDefaultValues(name, weight);

        // If coming from edit selection of exercise from inputActivity, then fill fields with existing data
        if (bundle.getBoolean("fromEdit")) {
            Log.d(TAG, "fromEdit: Editing existing workout from InputActivity");
            ArrayList<Rep> repsToEdit = bundle.getParcelableArrayList("repsToEditList");
            name = bundle.getString("exerciseName");
            inputSetTitle.setText(name);
            setValuesFromList(repsToEdit);
        }

        longDate = bundle.getLong("longDate", InputActivity.selectedIntegerDate);

        Log.d(TAG, "onCreateView: using longDate: " + longDate);

        Date d = new Date(longDate);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String stringDate = sdf.format(d);

        dateTextView = view.findViewById(R.id.dateTextView);
        dateTextView.setText(stringDate);

        addSetButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                if (setCount < 7)
                    addSetAndLayout();
                else
                    toastMessage("Cannot add another set.");
            }
        });

        removeSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setCount > 1)
                    removeSetAndLayout();
                else
                    toastMessage("Cannot remove last set.");
            }
        });

        saveSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbh = DatabaseHelper.getInstance(getActivity());

                Log.d(TAG, "saveSetButton pressed - longdate: " + longDate);

                // TODO: going to have to make this into a standalone activity not a tab to prevent access while creating an exercise or routine
                if (name == null) {
                    toastMessage("Can't save without exercise name");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }

                boolean emptyRep = false;
                List<Rep> sets = new ArrayList<>();
                for (int i = 0; i < setCount; i++) {
                    Rep newRep = new Rep();
                    newRep.setDate(longDate);
                    newRep.setExerciseID(((int) exerciseID));

                    if (repCounts.get(i).getText().toString().trim().length() == 0){
                        emptyRep = true;
                        toastMessage("Can't save a set with no reps.");
                        break;
                    } else {
                        emptyRep = false;
                        newRep.setNumReps(Integer.parseInt(repCounts.get(i).getText().toString()));
                        if (barTexts.get(i).getText().toString().trim().length() == 0)
                            newRep.setBarWeight(Float.valueOf(barTexts.get(i).getHint().toString()));
                        else
                            newRep.setBarWeight(Float.valueOf(barTexts.get(i).getText().toString()));
                        if (plateTexts.get(i).getText().toString().trim().length() == 0)
                            newRep.setPlateWeight(0.0f);
                        else
                            newRep.setPlateWeight(Float.valueOf(plateTexts.get(i).getText().toString()));

                        sets.add(newRep);
                    }
                }
                // if  at least one repCount field is empty, prevent set from being saved.
                if (!emptyRep) {
                    // this prevents duplicate when editing data, but messes up the order of the list
                    if(dbh.getRepsOnDateAndID(longDate, (int)exerciseID)!=null){
                        dbh.deleteRepsWithIDOnDate(longDate, (int)exerciseID);
                    }
                    for (Rep element : sets) {
                        dbh.createRep(element);
                        Log.d(TAG, "SET SAVED: " + element);
                    }
                    if (!MainActivity.getInstance().workoutEventExists(longDate)){
                        MainActivity.getInstance().addWorkoutEvent(longDate);
                    }
                    getActivity().finish();
                    Log.d(TAG, "onClick: workout saved");
                    toastMessage("Workout Saved");
                }
            }
        });

        return view;
    }

    public int dpToPx(int dp){
        float density = this.getActivity().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void addSetAndLayout(){
        setCount++;

        // Create linear layout
        LinearLayout newHorzLL = new LinearLayout(getActivity());
        newHorzLL.setId(newHorzLL.generateViewId());
        newHorzLL.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // set literal
        TextView tv = new TextView(getActivity());
        tv.setId(View.generateViewId());
        tv.setText("Set " + setCount + ":");
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f));
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        newHorzLL.addView(tv);

        // bar weight field
        EditText bar = new EditText(getActivity());
        bar.setId(View.generateViewId());
        bar.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(50),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f));
        bar.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        bar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        bar.setEms(10);
        bar.setHint(String.valueOf(set1DefaultWeight.getHint()));
        newHorzLL.addView(bar);
        barTexts.add(bar);

        // plate weight field
        EditText plate = new EditText(getActivity());
        plate.setId(View.generateViewId());
        plate.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(50),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f));
        plate.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        plate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        plate.setEms(10);
        newHorzLL.addView(plate);
        plateTexts.add(plate);

        // total weight field
        EditText total = new EditText(getActivity());
        total.setId(View.generateViewId());
        total.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(50),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f));
        total.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        total.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        total.setEms(10);
        newHorzLL.addView(total);
        totalTexts.add(total);

        // rep field
        EditText rep = new EditText(getActivity());
        rep.setId(View.generateViewId());
        LinearLayout.LayoutParams params  = new LinearLayout.LayoutParams(dpToPx(50),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f);
        params.setMargins(dpToPx(32), 0,0,0);
        rep.setLayoutParams(params);
        rep.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        rep.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        rep.setEms(10);
        newHorzLL.addView(rep);
        repCounts.add(rep);

        addMyListenerToEditText(bar, plate, total);
        setListLL.addView(newHorzLL);
    }

    @SuppressLint("NewApi")
    public void setValuesFromList(ArrayList<Rep> list){
        // add or remove set layouts depending on number of sets in list
        int sizeOfList = list.size();
        Log.d(TAG, "setValuesFromList: sizeOfList: " + sizeOfList + ", setCount: " + setCount);
        while (sizeOfList < setCount) {
            removeSetAndLayout();
            Log.d(TAG, "setValuesFromList: sizeOfList: " + sizeOfList + ", setCount: " + setCount);
        }
        while (sizeOfList > setCount) {
            addSetAndLayout();
            Log.d(TAG, "setValuesFromList: sizeOfList: " + sizeOfList + ", setCount: " + setCount);
        }

        exerciseID = list.get(0).getExerciseID();

        int count = 0;
        for (Rep item : list) {
            barTexts.get(count).setText(String.valueOf(item.getBarWeight()));
            plateTexts.get(count).setText(String.valueOf(item.getPlateWeight()));
            repCounts.get(count).setText(String.valueOf(item.getNumReps()));
            count++;
        }
    }

    public void removeSetAndLayout() {
        setCount--;
        setListLL.removeViewAt(setCount);
        barTexts.remove(barTexts.size() - 1);
        plateTexts.remove(plateTexts.size() - 1);
        totalTexts.remove(totalTexts.size() - 1);
        repCounts.remove(repCounts.size() - 1);
    }

    public void setDefaultValues(String name, float weight){
        inputSetTitle.setText(name);
        set1DefaultWeight.setHint(String.valueOf(weight));
        set2DefaultWeight.setHint(String.valueOf(weight));
        set3DefaultWeight.setHint(String.valueOf(weight));
    }

    private void addMyListenerToEditText(final EditText bar, final EditText plate, final EditText total){

        bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                float defaultWeight = Float.valueOf(bar.getHint().toString());
                float plateWeight = 0.0f;

                try {
                    defaultWeight = Float.valueOf(bar.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (plate.getText() != null && plate.getText().length() > 0) {
                    try {
                        plateWeight = Float.valueOf(plate.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "afterTextChanged: dw: " + defaultWeight + ", pw: " + plateWeight * 2);
                float totalWeight = defaultWeight + plateWeight * 2;
                total.setText(String.valueOf(totalWeight));
            }
        });
        plate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "afterTextChanged: " + new String(editable.toString()));
                float defaultWeight = Float.valueOf(bar.getHint().toString());
                float plateWeight = 0.0f;

                try {
                    defaultWeight = Float.valueOf(bar.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (plate.getText() != null && plate.getText().length() > 0) {
                    try {
                        plateWeight = Float.valueOf(plate.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "afterTextChanged: dw: " + defaultWeight + ", pw: " + plateWeight * 2);
                float totalWeight = defaultWeight + plateWeight * 2;
                if (plate.hasFocus())
                    total.setText(String.valueOf(totalWeight));
            }
        });
        total.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                float defaultWeight = Float.valueOf(bar.getHint().toString());
                float plateWeight = 0.0f;
                float totalWeight = 0.0f;

                try {
                    defaultWeight = Float.valueOf(bar.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    totalWeight = Float.valueOf(total.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                plateWeight = (totalWeight - defaultWeight) / 2;
                if (total.hasFocus())
                    plate.setText(String.valueOf(plateWeight));
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
