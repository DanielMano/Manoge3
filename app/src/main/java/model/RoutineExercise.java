package model;

import helper.DatabaseHelper;

/**
 * Created by Daniel on 5/10/2018.
 */

public class RoutineExercise {
    int routineID;
    int exerciseID;

    public RoutineExercise() {}

    public RoutineExercise(int routineID, int exerciseID) {
        this.routineID = routineID;
        this.exerciseID = exerciseID;
    }

    public int getRoutineID() {
        return routineID;
    }

    public void setRoutineID(int routineID) {
        this.routineID = routineID;
    }

    public int getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(int exerciseID) {
        this.exerciseID = exerciseID;
    }

    @Override
    public String toString() {
        return "Routine ID: " + routineID + ", Exercise ID: " + exerciseID;
    }
}
