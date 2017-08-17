package com.example.or.tonight;

/**
 * Created by Or on 20/07/2017.
 */

public class Place {

    private String name;
    private String downloadURL;
    private float distance;
    private int ages;


    public Place(String name, String downloadURL, float distance, int ages) {
        this.name = name;
        this.downloadURL = downloadURL;
        this.distance = distance;
        this.ages = ages;
    }

    public int getAges() {
        return ages;
    }

    public String getName() {
        return name;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public float getDistance() {
        return distance;
    }

    public void setAges(int ages) {
        this.ages = ages;
    }
}
