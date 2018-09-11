package com.example.daniel.manoge3;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import helper.DatabaseHelper;
import model.Exercise;
import model.Routine;
import model.RoutineExercise;

/**
 * Created by Daniel on 4/20/2018.
 */

public class InputItemTab3Routine extends Fragment{

    private static final String TAG = "InputItemTab3Routine";

    List<Exercise> list;
    List<Exercise> rList = new ArrayList<>();
    public InputItemTab3RoutineCustomAdapter adapter;
    public InputItemTab3RoutineCustomAdapter routineAdapter;
    Routine editableRoutine;
    List<Exercise> exerciseInRoutineToEdit;

    EditText routineName;

    private ListView routineList, exerciseList;
    DatabaseHelper dbh;

    private boolean initialView = false;
    private boolean editRoutine = false;

    @Override
    public void onResume() {
        super.onResume();
        populateLists();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (initialView)
                onResume();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_item_tab3_routine, container, false);

        initialView = true;

        dbh = DatabaseHelper.getInstance(getActivity());

        routineName = view.findViewById(R.id.routineNameField);

        routineList = view.findViewById(R.id.inRoutineList);
        exerciseList = view.findViewById(R.id.exerciseList);

        final Bundle receivedBundle = getActivity().getIntent().getExtras();
        if (receivedBundle.getParcelable("routineToEdit") != null) {
            onResume();
            editRoutine = true;
            editableRoutine = receivedBundle.getParcelable("routineToEdit");
            Log.d(TAG, "onCreateView: editableRoutine: " + editableRoutine);
            routineName.setText(editableRoutine.getName());
            exerciseInRoutineToEdit = dbh.getExercisesFromRoutine(editableRoutine.getID());
            Log.d(TAG, "onCreateView: exercises in editing routine: " + exerciseInRoutineToEdit);

            for (Exercise item : exerciseInRoutineToEdit) {
                rList.add(item);
                Log.d(TAG, "onCreateView: rList: " + rList);
                routineAdapter.notifyDataSetChanged();
            }
        }


        exerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Exercise exercise = list.get(position);
                rList.add(exercise);
                Log.d(TAG, "onItemClick: rList: " + rList);
                Log.d(TAG, "onItemClick: oldList: " + exerciseInRoutineToEdit);
                routineAdapter.notifyDataSetChanged();
            }
        });

        routineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                rList.remove(position);
                Log.d(TAG, "onItemClick: rList: " + rList);
                Log.d(TAG, "onItemClick: oldList: " + exerciseInRoutineToEdit);
                routineAdapter.notifyDataSetChanged();
            }
        });

        FloatingActionButton newRoutineFab = view.findViewById(R.id.newRoutineFab);
        newRoutineFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editRoutine) {
                    if (routineName.length() != 0) {
                        Routine newRoutine = new Routine();
                        String newRoutineName = routineName.getText().toString();

                        if (dbh.getRoutine(newRoutineName) != null) {
                            toastMessage("Routine with that name already exists");
                        } else {
                            newRoutine.setName(newRoutineName);
                            dbh.createRoutine(newRoutine);
                            Log.d(TAG, "onClick: created routine: " + newRoutine);
                            toastMessage("Routine " + newRoutine.getName() + " Created");

                            Routine createdRoutine = dbh.getRoutine(newRoutineName);
                            int createdRoutineID = createdRoutine.getID();
                            for (Exercise e : rList) {
                                dbh.createRoutinePair(createdRoutineID, e);
                                Log.d(TAG, "onClick: created routinePair: " + createdRoutineID + ", " + e.getName());
                            }

                            routineName.setText("");
                            rList.clear();
                            routineAdapter.notifyDataSetChanged();
                        }
                    } else {
                        toastMessage("Must enter name.");
                    }
                } else {
                    // if eID is in rList but not LIST, createPair
                    // if eID is in both, do nothing
                    // if eID is not in rList, but is in LIST, deletePair
                    ArrayList<Exercise> pairsToDelete = new ArrayList<>(exerciseInRoutineToEdit);
                    pairsToDelete.removeAll(rList);
                    Log.d(TAG, "onClick: exercises to delete: " + pairsToDelete);
                    for (Exercise item : pairsToDelete){
                        Log.d(TAG, "onClick: deleting: " + editableRoutine.getID() + " - " + item);
                        dbh.deletePair(editableRoutine.getID(), item);
                    }

                    ArrayList<Exercise> pairsToAdd = new ArrayList<>(rList);
                    pairsToAdd.removeAll(exerciseInRoutineToEdit);
                    Log.d(TAG, "onClick: exercises to add: " + pairsToAdd);
                    for (Exercise item : pairsToAdd) {
                        Log.d(TAG, "onClick: adding: " + editableRoutine.getID() + " - " + item);
                        dbh.createRoutinePair(editableRoutine.getID(), item);
                    }

                    Log.d(TAG, "onClick: final pairs: " + dbh.getAllPairsForRoutine(editableRoutine.getID()));
                    toastMessage("Routine " + editableRoutine.getName() + " Updated");

                    routineName.setText("");
                    rList.clear();
                    routineAdapter.notifyDataSetChanged();
                    getActivity().finish();
                }
            }
        });

        return view;
    }

    public void populateLists(){
        list = dbh.getAllExercises();
        //rList = new ArrayList<>();

        //adapter = new BeginWorkoutExerciseCustomAdapter(getActivity(), R.layout.begin_workout_exercise_listview_item, list);
        adapter = new InputItemTab3RoutineCustomAdapter(getActivity(), R.layout.activity_input_listview_item, list);
        exerciseList.setAdapter(adapter);

        //routineAdapter = new BeginWorkoutExerciseCustomAdapter(getActivity(), R.layout.begin_workout_exercise_listview_item, rList);
        routineAdapter = new InputItemTab3RoutineCustomAdapter(getActivity(), R.layout.activity_input_listview_item, rList);
        routineList.setAdapter(routineAdapter);
    }

    private void toastMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
