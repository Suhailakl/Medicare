package com.jrs.medicare;

/**
 * Created by SUHAIL on 11/2/2017.
 */

public class DocPrcptn {
    public DocPrcptn() {
    }

    public DocPrcptn(String doctor) {

        this.doctor = doctor;
    }

    public String doctor,date;

    public DocPrcptn(String doctor, String date) {
        this.doctor = doctor;
        this.date = date;
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
}
