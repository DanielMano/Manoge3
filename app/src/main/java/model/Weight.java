package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Daniel on 4/18/2018.
 */

public class Weight {

    int ID;
    long date;
    float bodyWeight;

    // constructors
    public Weight(){
    }

    public Weight(int ID, long date, float bodyWeight){
        this.ID = ID;
        this.date = date;
        this.bodyWeight = bodyWeight;
    }

    // setters
    public void setID(int ID){
        this.ID =ID;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setBodyWeight(float bodyWeight){
        this.bodyWeight = bodyWeight;
    }

    // getters
    public int getID(){
        return ID;
    }

    public long getDate(){
        return this.date;
    }

    public float getBodyWeight(){
        return this.bodyWeight;
    }

    @Override
    public String toString() {
        Date d = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String text = sdf.format(d);
        return text + " - " + bodyWeight;
    }
}
