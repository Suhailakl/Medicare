package com.jrs.medicare;

/**
 * Created by SUHAIL on 12/3/2017.
 */

public class UserNotModel {
    public String removedActive;
    public String doctorId;
    public String status;
    public String from;
    public String referredActive;
    public String date;
    public String docName;
    public String dActive,dId;

    public UserNotModel(String removedActive, String doctorId, String status, String from, String referredActive, String date, String docName, String dActive, String dId, String uid, String tokFullActive, long time) {
        this.removedActive = removedActive;
        this.doctorId = doctorId;
        this.status = status;
        this.from = from;
        this.referredActive = referredActive;
        this.date = date;
        this.docName = docName;
        this.dActive = dActive;
        this.dId = dId;
        this.uid = uid;
        this.tokFullActive = tokFullActive;
        this.time = time;
    }

    public String getdActive() {

        return dActive;
    }

    public void setdActive(String dActive) {
        this.dActive = dActive;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public UserNotModel(String removedActive, String doctorId, String status, String from, String referredActive, String date, String docName, String uid, String tokFullActive, long time) {
        this.removedActive = removedActive;
        this.doctorId = doctorId;
        this.status = status;
        this.from = from;
        this.referredActive = referredActive;
        this.date = date;
        this.docName = docName;
        this.uid = uid;
        this.tokFullActive = tokFullActive;
        this.time = time;
    }

    public String getDocName() {

        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UserNotModel(String removedActive, String doctorId, String status, String from, String referredActive, String date, String uid, String tokFullActive, long time) {
        this.removedActive = removedActive;
        this.doctorId = doctorId;
        this.status = status;
        this.from = from;
        this.referredActive = referredActive;
        this.date = date;

        this.uid = uid;
        this.tokFullActive = tokFullActive;
        this.time = time;
    }

    public String uid;

    public UserNotModel(String removedActive, String doctorId, String status, String from, String referredActive, String date, String tokFullActive, long time) {
        this.removedActive = removedActive;
        this.doctorId = doctorId;
        this.status = status;
        this.from = from;
        this.referredActive = referredActive;
        this.date = date;
        this.tokFullActive = tokFullActive;
        this.time = time;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTokFullActive() {
        return tokFullActive;
    }


    public void setTokFullActive(String tokFullActive) {
        this.tokFullActive = tokFullActive;
    }

    public String tokFullActive;
    public long time;

    public UserNotModel() {
    }

    public String getRemovedActive() {

        return removedActive;
    }

    public void setRemovedActive(String removedActive) {
        this.removedActive = removedActive;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReferredActive() {
        return referredActive;
    }

    public void setReferredActive(String referredActive) {
        this.referredActive = referredActive;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UserNotModel(String removedActive, String doctorId, String status, String from, String referredActive, long time) {

        this.removedActive = removedActive;
        this.doctorId = doctorId;
        this.status = status;
        this.from = from;
        this.referredActive = referredActive;
        this.time = time;
    }
}
