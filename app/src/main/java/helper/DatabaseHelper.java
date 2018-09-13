package helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import model.Exercise;
import model.Rep;
import model.Routine;
import model.RoutineExercise;
import model.Weight;

/**
 * Created by Daniel on 4/18/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static DatabaseHelper mInstance = null;

    private static final int DATABASE_VERSION = 6;


    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }


    private static final String DATABASE_NAME = "manogeDatabase";

    // Table names
    private static final String TABLE_WEIGHT = "weights";
    private static final String TABLE_EXERCISE = "exercises";
    private static final String TABLE_REP = "reps";
    private static final String TABLE_ROUTINE = "routines";
    private static final String TABLE_ROUTINE_REL = "routine_exercises";

    // Common column names
    private static final String KEY_ID = "id";

    // WEIGHT Table - column names
    private static final String KEY_WEIGHT = "bodyWeight";
    private static final String KEY_DATE = "date";

    // EXERCISE Table - column names
    private static final String KEY_EXERCISE_NAME = "name";
    private static final String KEY_DEFAULT_WEIGHT = "defaultWeight";

    // REP Table - column names
    private static final String KEY_EXERCISE_ID = "exerciseID";
    private static final String KEY_WEIGHT_DATE = "date";
    private static final String KEY_BAR = "barWeight";
    private static final String KEY_PLATE = "plateWeight";
    private static final String KEY_NUMREPS = "numReps";

    // ROUTINE Table - column names
    private static final String KEY_ROUTINE_NAME = "name";
    
    // ROUTINE REL Table - column names
    private static final String KEY_ROUTINE_ID = "routineID";
    // KEY_EXERCISE_ID
    

    // Table Create Statements
    // WEIGHT Table - create
    private static final String CREATE_TABLE_WEIGHT = "CREATE TABLE " + TABLE_WEIGHT + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_DATE + " INTEGER, " +
            KEY_WEIGHT + " NUMERIC(5,2))";

    // EXERCISE Table - create
    private static final String CREATE_TABLE_EXERCISE = "CREATE TABLE " + TABLE_EXERCISE + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_EXERCISE_NAME + " TEXT, " +
            KEY_DEFAULT_WEIGHT + " NUMERIC(5,2))";

    // REP Table - create
    private static final String CREATE_TABLE_REP = "CREATE TABLE " + TABLE_REP + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_EXERCISE_ID + " INTEGER, " +
            KEY_WEIGHT_DATE + " DATETIME, " +
            KEY_BAR + " NUMERIC(5,2), " +
            KEY_PLATE + " NUMERIC(5,2), " +
            KEY_NUMREPS + " INTEGER)";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // creating tables
        sqLiteDatabase.execSQL(CREATE_TABLE_WEIGHT);
        sqLiteDatabase.execSQL(CREATE_TABLE_EXERCISE);
        sqLiteDatabase.execSQL(CREATE_TABLE_REP);
    }


    // -- onUpgrade statements --
    // version 3 statements
    private static final String DATABASE_ALTER_EXERCISE_1 = "ALTER TABLE " +
            TABLE_EXERCISE + " ADD COLUMN " + KEY_DEFAULT_WEIGHT + " NUMERIC(5,2);";

    // version 4 statements
    private static final String CREATE_TABLE_ROUTINE = "CREATE TABLE " + TABLE_ROUTINE + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_ROUTINE_NAME + " TEXT)";

    private static final String CREATE_TABLE_ROUTINE_REL = "CREATE TABLE " + TABLE_ROUTINE_REL + "(" +
            KEY_ROUTINE_ID + " INTEGER NOT NULL, " +
            KEY_EXERCISE_ID + " INTEGER NOT NULL, " +
            "PRIMARY KEY (" + KEY_ROUTINE_ID + ", " + KEY_EXERCISE_ID + "), " +
            "FOREIGN KEY (" + KEY_EXERCISE_ID + ") REFERENCES " + TABLE_EXERCISE + "(" + KEY_ID + "), " +
            "FOREIGN KEY (" + KEY_ROUTINE_ID + ") REFERENCES " + TABLE_ROUTINE + "(" + KEY_ID + "))";

    private static final String RENAME_REP_TABLE = "ALTER TABLE " + TABLE_REP +
            " RENAME TO tempRep";

    private static final String CREATE_REP_TABLE_WITH_FK = "CREATE TABLE " + TABLE_REP + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_EXERCISE_ID + " INTEGER, " +
            KEY_WEIGHT_DATE + " DATETIME, " +
            KEY_BAR + " NUMERIC(5,2), " +
            KEY_PLATE + " NUMERIC(5,2), " +
            KEY_NUMREPS + " INTEGER, " +
            "FOREIGN KEY (" + KEY_EXERCISE_ID + ") REFERENCES " + TABLE_EXERCISE + "(" + KEY_ID + "))";

    private static final String COPY_REP = "INSERT INTO " + TABLE_REP + " SELECT * FROM tempRep";

    private static final String DROP_TEMP_REP_TABLE = "DROP TABLE tempRep";

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: oldVersion: " + oldVersion + ", newVersion: " + newVersion);
        if (oldVersion < 3) {
            sqLiteDatabase.execSQL(DATABASE_ALTER_EXERCISE_1);
            Log.d(TAG, "onUpgrade: added default weight column to Exercise table");
        }
        if (oldVersion < 6) {
            sqLiteDatabase.execSQL(CREATE_TABLE_ROUTINE);
            Log.d(TAG, "onUpgrade: created Routine table");
            sqLiteDatabase.execSQL(CREATE_TABLE_ROUTINE_REL);
            Log.d(TAG, "onUpgrade: created Routine Exercises table");
            sqLiteDatabase.execSQL(RENAME_REP_TABLE);
            Log.d(TAG, "onUpgrade: renamed Rep table to tempRep");
            sqLiteDatabase.execSQL(CREATE_REP_TABLE_WITH_FK);
            Log.d(TAG, "onUpgrade: created new Rep table with add foreign key");
            sqLiteDatabase.execSQL(COPY_REP);
            Log.d(TAG, "onUpgrade: copy data from tempRep into Rep");
            sqLiteDatabase.execSQL(DROP_TEMP_REP_TABLE);
            Log.d(TAG, "onUpgrade: drop tempRep table");
        }
    }

    // ------------------------------------------------------------------------

    //
    // WEIGHT Table - methods
    //

    // create weight item
    public long createWeight(Weight weight){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, weight.getDate());
        values.put(KEY_WEIGHT, weight.getBodyWeight());

        long weightID = db.insert(TABLE_WEIGHT, null, values);

        return weightID;
    }

    // get a single date, weight
    public Weight getWeight(long weightID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_WEIGHT +
                " WHERE " + KEY_ID + " = " + weightID;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null){
            c.moveToFirst();
        }

        Weight newWeight = new Weight();
        newWeight.setID(c.getInt(c.getColumnIndex(KEY_ID)));
        newWeight.setDate(c.getInt(c.getColumnIndex(KEY_DATE)));
        newWeight.setBodyWeight(c.getFloat(c.getColumnIndex(KEY_WEIGHT)));

        c.close();

        return newWeight;
    }

    // return cursor of all weights for graph
    public Cursor getWeightsForGraph(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_WEIGHT + " ORDER BY " + KEY_DATE;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // returns cursor if specific date exists in database
    public Cursor getDate(long date){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_WEIGHT + " WHERE " + KEY_DATE + " = " + date;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // get all weights
    public List<Weight> getAllWeights() {
        List<Weight> weights = new ArrayList<Weight>();
        String selectQuery = "SELECT * FROM " + TABLE_WEIGHT + " ORDER BY Date";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to list
        if (c.moveToFirst()) {
            do {
                Weight w = new Weight();
                w.setID(c.getInt(c.getColumnIndex(KEY_ID)));
                w.setDate(c.getLong(c.getColumnIndex(KEY_DATE)));
                w.setBodyWeight(c.getFloat(c.getColumnIndex(KEY_WEIGHT)));

                weights.add(w);
            } while (c.moveToNext());
        }

        c.close();
        return weights;
    }

    // update a weight
    public void updateWeight(Weight w){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_WEIGHT +
                " SET " + KEY_WEIGHT + " = " + w.getBodyWeight() +
                " WHERE " + KEY_DATE + " = " + w.getDate();
        Log.d(TAG, "updateWeight: " + query);
        db.execSQL(query);
    }

    // delete a weight
    public void deleteWeight(long date) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_WEIGHT +
                " WHERE " + KEY_DATE + " = " + date;
        db.execSQL(query);
        Log.d(TAG, "deleteWeight: date being deleted " + date);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    //
    // EXERCISE table - methods
    //

    // creates a new exercise item
    public long createExercise(Exercise exercise){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_EXERCISE_NAME, exercise.getName());
        values.put(KEY_DEFAULT_WEIGHT, exercise.getBarWeight());

        long weightID = db.insert(TABLE_EXERCISE, null, values);

        return weightID;
    }

    // returns one specific exercise
    public Exercise getExercise(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_EXERCISE +
                " WHERE " + KEY_EXERCISE_NAME + " = '" + name + "'";

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null){
            c.moveToFirst();
        }

        Exercise newExercise = new Exercise();
        newExercise.setID(c.getInt(c.getColumnIndex(KEY_ID)));
        newExercise.setName(c.getString(c.getColumnIndex(KEY_EXERCISE_NAME)));
        newExercise.setBarWeight(c.getFloat(c.getColumnIndex(KEY_DEFAULT_WEIGHT)));
        c.close();
        return newExercise;
    }

    // get all exercises
    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<Exercise>();
        String selectQuery = "SELECT * FROM " + TABLE_EXERCISE + " ORDER BY name";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                Exercise newExercise = new Exercise();
                newExercise.setID(c.getInt(c.getColumnIndex(KEY_ID)));
                newExercise.setName(c.getString(c.getColumnIndex(KEY_EXERCISE_NAME)));
                newExercise.setBarWeight(c.getFloat(c.getColumnIndex(KEY_DEFAULT_WEIGHT)));
                exercises.add(newExercise);
            } while (c.moveToNext());
        }
        c.close();
        return exercises;
    }

    // update exercise
    public void updateExercise(Exercise e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE_NAME, e.getName());
        values.put(KEY_DEFAULT_WEIGHT, e.getBarWeight());
        db.update(TABLE_EXERCISE, values,"id=?", new String[]{String.valueOf(e.getID())});
    }

    // delete an exercise
    public void deleteExercise(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_EXERCISE, "name=?", new String[]{ name });
    }

    // check if exercise exists
    public boolean checkIfExerciseExists(String name){
        boolean exerciseExists = false;

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + KEY_ID + " FROM " + TABLE_EXERCISE + " WHERE " + KEY_EXERCISE_NAME + " = '" + name + "'";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null) {
            if (cursor.getCount()>0) {
                exerciseExists = true;
            }
        }

        cursor.close();
        db.close();

        return exerciseExists;
    }

    public Cursor getMatchingExercises(String searchTerm) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT id _id,* FROM " + TABLE_EXERCISE +
                " WHERE " + KEY_EXERCISE_NAME + " LIKE '%" + searchTerm + "%'" +
                " ORDER BY " + KEY_EXERCISE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor;
        }

        return null;
    }

    //
    // REP table - methods
    //

    // create new rep
    public long createRep(Rep rep){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_DATE, rep.getDate());
        values.put(KEY_EXERCISE_ID, rep.getExerciseID());
        values.put(KEY_BAR, rep.getBarWeight());
        values.put(KEY_PLATE, rep.getPlateWeight());
        values.put(KEY_NUMREPS, rep.getNumReps());

        long repID = db.insert(TABLE_REP, null, values);

        return repID;
    }

    // get all reps
    public List<Rep> getAllReps() {
        List<Rep> reps = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_REP + " ORDER BY Date";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to list
        if (c.moveToFirst()) {
            do {
                Rep r = new Rep();
                r.setID(c.getInt(c.getColumnIndex(KEY_ID)));
                r.setExerciseID(c.getInt(c.getColumnIndex(KEY_EXERCISE_ID)));
                r.setDate(c.getLong(c.getColumnIndex(KEY_DATE)));
                r.setBarWeight(c.getFloat(c.getColumnIndex(KEY_BAR)));
                r.setPlateWeight(c.getFloat(c.getColumnIndex(KEY_PLATE)));
                r.setNumReps(c.getInt(c.getColumnIndex(KEY_NUMREPS)));

                reps.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return reps;
    }

    // get all reps on specific date
    public List<Rep> getRepsOnDate(long date) {
        List<Rep> reps = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_REP +
                " WHERE " + KEY_DATE + " = " + date +
                " ORDER BY " + KEY_DATE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Rep r = new Rep();
                r.setID(c.getInt(c.getColumnIndex(KEY_ID)));
                r.setExerciseID(c.getInt(c.getColumnIndex(KEY_EXERCISE_ID)));
                r.setDate(c.getLong(c.getColumnIndex(KEY_DATE)));
                r.setBarWeight(c.getFloat(c.getColumnIndex(KEY_BAR)));
                r.setPlateWeight(c.getFloat(c.getColumnIndex(KEY_PLATE)));
                r.setNumReps(c.getInt(c.getColumnIndex(KEY_NUMREPS)));

                reps.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return reps;
    }

    // get all reps on specific date
    public ArrayList<Rep> getRepsOnDateAndID(long date, int id) {
        ArrayList<Rep> reps = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_REP +
                " WHERE " + KEY_DATE + " = " + date +
                " AND " + KEY_EXERCISE_ID + " = " + id +
                " ORDER BY " + KEY_DATE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Rep r = new Rep();
                r.setID(c.getInt(c.getColumnIndex(KEY_ID)));
                r.setExerciseID(c.getInt(c.getColumnIndex(KEY_EXERCISE_ID)));
                r.setDate(c.getLong(c.getColumnIndex(KEY_DATE)));
                r.setBarWeight(c.getFloat(c.getColumnIndex(KEY_BAR)));
                r.setPlateWeight(c.getFloat(c.getColumnIndex(KEY_PLATE)));
                r.setNumReps(c.getInt(c.getColumnIndex(KEY_NUMREPS)));

                reps.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return reps;
    }

    public List<String> joinRepExerciseOnDate(long date) {
        List<String> items = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_REP + " INNER JOIN " + TABLE_EXERCISE +
                " ON " + TABLE_REP + "." + KEY_EXERCISE_ID + " = " + TABLE_EXERCISE + "." + KEY_ID +
                " WHERE " + TABLE_REP + "." + KEY_DATE + " = " + date;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()){
            do{
                String item = "";
                item += c.getString(c.getColumnIndex(KEY_EXERCISE_NAME));
                item += "-" + c.getFloat(c.getColumnIndex(KEY_BAR));
                item += "-" + c.getFloat(c.getColumnIndex(KEY_PLATE));
                item += "-" + c.getInt(c.getColumnIndex(KEY_NUMREPS));

                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return items;
    }

    // delete all sets with ID on DATE
    public void deleteRepsWithIDOnDate(long date, int exerciseID) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_REP, "date=? AND exerciseID=?", new String[]{ String.valueOf(date), String.valueOf(exerciseID) });
    }

    //
    // ROUTINE table - methods
    //

    // create new routine
    public void createRoutine(Routine routine) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ROUTINE_NAME, routine.getName());

        db.insert(TABLE_ROUTINE, null, values);
    }

    // get routine
    public Routine getRoutine(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_ROUTINE +
                " WHERE " + KEY_ROUTINE_NAME + " = '" + name + "'";
        Cursor c = db.rawQuery(query, null);

        if (c.getCount() == 0){
            Log.d(TAG, "getRoutine: returning null");
            c.close();
            return null;
        } else {
            c.moveToFirst();
            Routine r = new Routine();
            r.setID(c.getInt(c.getColumnIndex(KEY_ID)));
            r.setName(c.getString(c.getColumnIndex(KEY_ROUTINE_NAME)));

            c.close();

            return r;
        }
    }

    public String getRoutine(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_ROUTINE +
                " WHERE " + KEY_ID + " = " + ID;
        Cursor c = db.rawQuery(query, null);

        String name = "";
        if (c != null && c.moveToFirst()){
            name += c.getString(c.getColumnIndex(KEY_ROUTINE_NAME));
            c.close();
        }

        return name;
    }

    // get all routines
    public List<Routine> getAllRoutines() {
        List<Routine> routines = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ROUTINE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to list
        if (c.moveToFirst()) {
            do {
                Routine r = new Routine();
                r.setID(c.getInt(c.getColumnIndex(KEY_ID)));
                r.setName(c.getString(c.getColumnIndex(KEY_ROUTINE_NAME)));

                routines.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return routines;
    }

    // delete all routines
    public void deleteAllRoutines(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_ROUTINE);
        Log.d(TAG, "deleteAllRoutines: deleted all routines");
        db.execSQL("DELETE FROM " + TABLE_ROUTINE_REL);
    }

    public void deleteRoutine(int routineID) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ROUTINE, "id=?", new String[]{ String.valueOf(routineID) });
    }

    //
    // ROUTINE REL table - methods
    //

    public void createRoutinePair(int routineID, Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ROUTINE_ID, routineID);
        values.put(KEY_EXERCISE_ID, exercise.getID());

        db.insert(TABLE_ROUTINE_REL, null, values);
    }

    public int getExerciseCount(int routineID){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(" + KEY_ROUTINE_ID + ") FROM " + TABLE_ROUTINE_REL +
                " WHERE " + KEY_ROUTINE_ID + " = " + routineID;

        String[] args = { String.valueOf(routineID) };
        int count = (int) DatabaseUtils.queryNumEntries(db, TABLE_ROUTINE_REL, KEY_ROUTINE_ID + " = ?", args);
        return count;
    }

    public ArrayList<RoutineExercise> getAllPairs(){
        ArrayList<RoutineExercise> pairs = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ROUTINE_REL;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to list
        if (c.moveToFirst()) {
            do {
                RoutineExercise r = new RoutineExercise();
                r.setRoutineID(c.getInt(c.getColumnIndex(KEY_ROUTINE_ID)));
                r.setExerciseID(c.getInt(c.getColumnIndex(KEY_EXERCISE_ID)));

                pairs.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return pairs;
    }

    public ArrayList<RoutineExercise> getAllPairsForRoutine(int routineID){
        ArrayList<RoutineExercise> pairs = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ROUTINE_REL + " WHERE " + KEY_ROUTINE_ID + " = " + routineID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to list
        if (c.moveToFirst()) {
            do {
                RoutineExercise r = new RoutineExercise();
                r.setRoutineID(c.getInt(c.getColumnIndex(KEY_ROUTINE_ID)));
                r.setExerciseID(c.getInt(c.getColumnIndex(KEY_EXERCISE_ID)));

                pairs.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return pairs;
    }

    public List<Exercise> getExercisesFromRoutine(int routineID){
        List<Exercise> exercises = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ROUTINE_REL + " INNER JOIN " + TABLE_EXERCISE +
                " ON " + TABLE_ROUTINE_REL + "." + KEY_EXERCISE_ID + " = " + TABLE_EXERCISE + "." + KEY_ID +
                " WHERE " + TABLE_ROUTINE_REL + "." + KEY_ROUTINE_ID + " = " + routineID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                Exercise newExercise = new Exercise();
                newExercise.setID(c.getInt(c.getColumnIndex(KEY_ID)));
                newExercise.setName(c.getString(c.getColumnIndex(KEY_EXERCISE_NAME)));
                newExercise.setBarWeight(c.getFloat(c.getColumnIndex(KEY_DEFAULT_WEIGHT)));
                exercises.add(newExercise);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return exercises;
    }

    public void deletePair(int routineID, Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ROUTINE_REL, "routineID=? AND exerciseID=?", new String[]{ String.valueOf(routineID), String.valueOf(exercise.getID()) });
    }

    public void deletePairs(Routine routine) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ROUTINE_REL, "routineID=?", new String[]{ String.valueOf(routine.getID()) });
    }
}
