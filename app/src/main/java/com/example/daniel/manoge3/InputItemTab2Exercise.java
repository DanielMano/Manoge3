package com.example.daniel.manoge3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import helper.DatabaseHelper;
import model.Exercise;

/**
 * Created by Daniel on 4/22/2018.
 */

public class InputItemTab2Exercise extends Fragment {
    private static final String TAG = "InputItemTab2Exercise";

    AutoCompleteTextView exerciseNameAutoComplete;
    EditText barWeightLiteral;

    DatabaseHelper dbh;

    String[] item = new String[]{"Please search..."};

    String editExName;
    int editExId;
    float editExWeight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_item_tab2_exercise, container, false);

        dbh = DatabaseHelper.getInstance(getActivity());

        exerciseNameAutoComplete = view.findViewById(R.id.autoCompleteTextView);
        barWeightLiteral = view.findViewById(R.id.barWeightField);

        ItemAutoTextAdapter adapter = this.new ItemAutoTextAdapter(dbh);
        exerciseNameAutoComplete.setAdapter(adapter);
        exerciseNameAutoComplete.setOnClickListener(adapter);

        final Bundle bundle = getActivity().getIntent().getExtras();
        final Exercise exToEdit = bundle.getParcelable("exerciseToEdit");
        Log.d(TAG, "onCreateView: bundle from edit: " + bundle.getBoolean("fromEditExercise"));

        if (bundle.getBoolean("fromEditExercise")) {
            editExName = exToEdit.getName();
            editExId = exToEdit.getID();
            editExWeight = exToEdit.getBarWeight();

            barWeightLiteral.setText(String.valueOf(editExWeight));
            exerciseNameAutoComplete.setText(editExName);
        }

        // submit new exercise to database
        FloatingActionButton newExerciseFab = view.findViewById(R.id.newExerciseFab);
        newExerciseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = exerciseNameAutoComplete.getText().toString();
                if(name.length() == 0){
                    toastMessage("You must enter a name.");
                } else {
                    //TODO: search database for name because I can't finish() the fragment so the user can repeatedly push enter button

                    // set default weight to 45 and update if changed by user
                    float defaultWeight = 45.0f;
                    try {
                        defaultWeight = Float.valueOf(barWeightLiteral.getText().toString());
                    } catch (Exception e) {
                        Log.d(TAG, "onClick: something went wrong with the default weight");
                    }

                    if (bundle.getBoolean("fromEditExercise")) {
                        Exercise editedExercise = new Exercise(editExId, name, defaultWeight);
                        dbh.updateExercise(editedExercise);
                        toastMessage("Updated " + editedExercise.getName());
                        Log.d(TAG, "updateExercise from: " + exToEdit + " to " + editedExercise);
                    } else {
                        Exercise newEx = new Exercise();
                        newEx.setName(name);
                        newEx.setBarWeight(defaultWeight);

                        dbh.createExercise(newEx);
                        toastMessage("Exercise " + newEx.getName() + " Created");
                        Log.d(TAG, "createExercise: " + name + ", " + defaultWeight);
                    }
                    exerciseNameAutoComplete.setText("");
                    barWeightLiteral.setText("");
                    barWeightLiteral.setHint("45");
                    barWeightLiteral.clearFocus();
                }
            }
        });

        return view;
    }

    private void toastMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


    public class ItemAutoTextAdapter extends CursorAdapter implements android.widget.AdapterView.OnClickListener {
        private DatabaseHelper dbh;

        public ItemAutoTextAdapter(DatabaseHelper dbh) {
            super(InputItemTab2Exercise.this.getActivity(), null);
            this.dbh = dbh;
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null) {
                return getFilterQueryProvider().runQuery(constraint);
            }

            Cursor cursor = dbh.getMatchingExercises(constraint != null ? constraint.toString() : null);
            return cursor;
        }

        @Override
        public String convertToString(Cursor cursor) {
            final int columnIndex = cursor.getColumnIndex("name");
            final String str = cursor.getString(columnIndex);
            return str;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final String text = convertToString(cursor);
            ((TextView) view).setText(text);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, viewGroup, false);
            return view;
        }

        @Override
        public void onClick(View view) {

        }
    }
}
