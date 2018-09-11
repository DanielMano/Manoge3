package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Daniel on 4/18/2018.
 */

public class Rep implements Parcelable{

    int ID;
    long date;
    int exerciseID;
    float barWeight;
    float plateWeight;
    int numReps;

    // constructors
    public Rep(){}

    public Rep(int ID, long date, int exerciseID, int numReps){
        this.ID = ID;
        this.date = date;
        this.exerciseID = exerciseID;
        this.numReps = numReps;
    }

    public Rep(int ID, long date, int exerciseID, float plateWeight, int numReps){
        this.ID = ID;
        this.date = date;
        this.exerciseID = exerciseID;
        this.plateWeight = plateWeight;
        this.numReps = numReps;
    }

    public Rep(int ID, long date, int exerciseID, float barWeight, float plateWeight, int numReps){
        this.ID = ID;
        this.date = date;
        this.exerciseID = exerciseID;
        this.barWeight = barWeight;
        this.plateWeight = plateWeight;
        this.numReps = numReps;
    }

    // setters
    public void setID(int ID) {
        this.ID = ID;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public void setExerciseID(int exerciseID) {
        this.exerciseID = exerciseID;
    }
    public void setBarWeight(float barWeight) {
        this.barWeight = barWeight;
    }
    public void setPlateWeight(float plateWeight) {
        this.plateWeight = plateWeight;
    }
    public void setNumReps(int numReps) {
        this.numReps = numReps;
    }

    // getters
    public int getID() {
        return ID;
    }
    public long getDate() {
        return date;
    }
    public int getExerciseID() {
        return exerciseID;
    }
    public float getBarWeight() {
        return barWeight;
    }
    public float getPlateWeight() {
        return plateWeight;
    }
    public int getNumReps() {
        return numReps;
    }

    @Override
    public String toString() {
        Date d = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String text = sdf.format(d);

        return text + "  -  eID: " + exerciseID + "\nweight: " + (barWeight + plateWeight * 2) +
                "  -  #r: " + numReps;
    }

    protected Rep (Parcel in) {
        ID = in.readInt();
        date = in.readLong();
        exerciseID = in.readInt();
        barWeight = in.readFloat();
        plateWeight = in.readFloat();
        numReps = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeLong(date);
        parcel.writeInt(exerciseID);
        parcel.writeFloat(barWeight);
        parcel.writeFloat(plateWeight);
        parcel.writeInt(numReps);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Rep> CREATOR = new Parcelable.Creator<Rep>() {
        @Override
        public Rep createFromParcel(Parcel in) {
            return new Rep(in);
        }

        @Override
        public Rep[] newArray(int size) {
            return new Rep[size];
        }
    };
}
