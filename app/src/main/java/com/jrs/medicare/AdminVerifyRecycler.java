package com.jrs.medicare;

/**
 * Created by SUHAIL on 11/17/2017.
 */

public class AdminVerifyRecycler {
    public String name;
    public String city;

    public AdminVerifyRecycler(String name, String city, String uid, long timeStamp) {
        this.name = name;
        this.city = city;
        this.uid = uid;
        this.timeStamp = timeStamp;
    }

    public String getUid() {

        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String uid;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public AdminVerifyRecycler(String name, String city, long timeStamp) {

        this.name = name;
        this.city = city;
        this.timeStamp = timeStamp;
    }

    public long timeStamp;

    public AdminVerifyRecycler() {
    }

    public AdminVerifyRecycler(String name, String city) {

        this.name = name;
        this.city = city;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
