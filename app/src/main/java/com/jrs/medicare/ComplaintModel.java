package com.jrs.medicare;

/**
 * Created by SUHAIL on 11/19/2017.
 */

public class ComplaintModel {
    public String complaint;
    public String docKey;
    public String issue;
    public String uid;

    public ComplaintModel(String complaint, String docKey, String issue, String uid, String active, long timeStamp) {
        this.complaint = complaint;
        this.docKey = docKey;
        this.issue = issue;
        this.uid = uid;
        this.active = active;
        this.timeStamp = timeStamp;
    }

    public String getActive() {

        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String active;
    public long timeStamp;

    public ComplaintModel() {
    }

    public ComplaintModel(String complaint, String docKey, String issue, String uid, long timeStamp) {

        this.complaint = complaint;
        this.docKey = docKey;
        this.issue = issue;
        this.uid = uid;
        this.timeStamp = timeStamp;
    }

    public String getComplaint() {

        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getDocKey() {
        return docKey;
    }

    public void setDocKey(String docKey) {
        this.docKey = docKey;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
