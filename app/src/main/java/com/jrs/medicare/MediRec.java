package com.jrs.medicare;

/**
 * Created by SUHAIL on 10/28/2017.
 */

public class MediRec {
    public String patient,date,userId,uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public MediRec(String patient, String date, String userId, String uid) {
        this.patient = patient;
        this.date = date;
        this.userId = userId;
        this.uid = uid;
    }

    public MediRec() {
    }

    public MediRec(String patient, String date, String userId) {

        this.patient = patient;
        this.date = date;
        this.userId = userId;
    }

    public String getPatient() {
        return patient;

    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
