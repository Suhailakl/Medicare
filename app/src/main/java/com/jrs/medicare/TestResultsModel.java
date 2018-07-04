package com.jrs.medicare;

/**
 * Created by SUHAIL on 11/10/2017.
 */

public class TestResultsModel {
    public String image;

    public TestResultsModel(String image, String referred, long time, String subject, String name, String docKey, String date, String mRkey) {
        this.image = image;
        this.referred = referred;
        this.time = time;
        this.subject = subject;
        this.name = name;
        this.docKey = docKey;
        this.date = date;
        this.mRkey = mRkey;
    }

    public String getReferred() {

        return referred;
    }

    public void setReferred(String referred) {
        this.referred = referred;
    }

    public String referred;
    public long time;

    public TestResultsModel(String image, long time, String subject, String name, String docKey, String date) {
        this.image = image;
        this.time = time;
        this.subject = subject;
        this.name = name;
        this.docKey = docKey;
        this.date = date;
    }

    public long getTime() {

        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public TestResultsModel() {
    }

    public String subject;
    public String name;
    public String docKey;
    public String date;
    public String mRkey;

    public TestResultsModel(String image, long time, String subject, String name, String docKey, String date, String mRkey) {
        this.image = image;
        this.time = time;
        this.subject = subject;
        this.name = name;
        this.docKey = docKey;
        this.date = date;
        this.mRkey = mRkey;
    }

    public String getmRkey() {

        return mRkey;
    }

    public void setmRkey(String mRkey) {
        this.mRkey = mRkey;
    }

    public TestResultsModel(String image, String subject, String name, String docKey, String date) {
        this.image = image;
        this.subject = subject;
        this.name = name;
        this.docKey = docKey;
        this.date = date;
    }

    public String getImage() {

        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocKey() {
        return docKey;
    }

    public void setDocKey(String docKey) {
        this.docKey = docKey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
