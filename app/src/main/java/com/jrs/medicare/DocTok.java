package com.jrs.medicare;

/**
 * Created by SUHAIL on 9/29/2017.
 */

public class DocTok {
    public DocTok() {
    }
    public String name;
    public String doctor;
    public String date;
    public String time1;
    public String time2;
    public String token;
    public String uid;

    public DocTok(String name, String doctor, String date, String time1, String time2, String token, String uid, String doctorId) {
        this.name = name;
        this.doctor = doctor;
        this.date = date;
        this.time1 = time1;
        this.time2 = time2;
        this.token = token;
        this.uid = uid;
        this.doctorId = doctorId;
    }

    public String getUid() {

        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public DocTok(String name, String doctor, String date, String time1, String time2, String token, String doctorId) {

        this.name = name;
        this.doctor = doctor;
        this.date = date;
        this.time1 = time1;
        this.time2 = time2;
        this.token = token;
        this.doctorId = doctorId;
    }

    public String getDoctorId() {

        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String doctorId;

    public DocTok(String name, String doctor, String date, String time1, String time2, String token) {
        this.name = name;
        this.doctor = doctor;
        this.date = date;
        this.time1 = time1;
        this.time2 = time2;
        this.token = token;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
