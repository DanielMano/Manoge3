package model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel on 4/18/2018.
 */

public class Exercise implements Parcelable {

    int ID;
    String name;
    float barWeight;

    // constructors
    public Exercise(){

    }
    public Exercise(int ID, String name){
        this.ID = ID;
        this.name = name;
        barWeight = 45.0f;
    }

    public Exercise(int ID, String name, float barWeight){
        this.ID = ID;
        this.name = name;
        this.barWeight = barWeight;
    }

    // setter
    public void setID(int ID){
        this.ID = ID;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setBarWeight(float barWeight) {
        this.barWeight = barWeight;
    }

    // getters
    public int getID(){
        return this.ID;
    }

    public String getName(){
        return this.name;
    }

    public float getBarWeight(){
        return this.barWeight;
    }

    @Override
    public String toString() {
        return name + " - " + barWeight;
    }

    public String toStringName(){
        return name;
    }

    protected Exercise(Parcel in) {
        ID = in.readInt();
        name = in.readString();
        barWeight = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(name);
        dest.writeFloat(barWeight);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Exercise> CREATOR = new Parcelable.Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };
}
