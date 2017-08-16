package com.example.or.tonight;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Created by Or on 25/07/2017.
 */

public class PersonGoing {
    private String name;
    private String gender;
    private int age;
    private long time;

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setAgeByBirth(long date) {
        Calendar a = Calendar.getInstance();
        Date d = new Date(date);

        Calendar b = Calendar.getInstance();
        b.setTime(d);

        Log.i("hiiiiiiiiii", "a: " + a.get(Calendar.DAY_OF_MONTH) + "," + (a.get(Calendar.MONTH) + 1) + "," + a.get(Calendar.YEAR));
        Log.i("hiiiiiiiiii", "b: " + b.get(Calendar.DAY_OF_MONTH) + "," + (b.get(Calendar.MONTH) + 1) + "," + b.get(Calendar.YEAR));

        int diff = a.get(YEAR) - b.get(YEAR);
        if (b.get(MONTH) > a.get(MONTH) || (b.get(MONTH) == a.get(MONTH) && b.get(Calendar.DAY_OF_MONTH) > a.get(Calendar.DAY_OF_MONTH))) {
            diff--;
        }
        Log.i("hiiiiiiiii", "difference: " + diff);

        this.age = diff;
    }

    public PersonGoing(){

    }

    public PersonGoing(String name, String gender, int age, long time) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }
}
