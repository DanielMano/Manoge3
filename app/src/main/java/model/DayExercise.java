package model;

/**
 * Created by Daniel on 8/27/2018.
 */

public class DayExercise {

    String name;
    String sets;

    public DayExercise(String name, String sets) {
        this.name = name;
        this.sets = sets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }
}
