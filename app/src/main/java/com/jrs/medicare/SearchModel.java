package com.jrs.medicare;

/**
 * Created by SUHAIL on 11/4/2017.
 */

public class SearchModel {
    public SearchModel() {
    }

    public String getCity() {

        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public SearchModel(String city) {

        this.city = city;
    }

    public String city;

    public SearchModel(String city, String uid, String name, String department) {

        this.city = city;
        this.uid = uid;
        this.name = name;
        this.department = department;
    }

    public String getUid() {

        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String uid;

    public SearchModel(String city, String name, String department) {
        this.city = city;
        this.name = name;
        this.department = department;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name;

    public SearchModel(String city, String department) {

        this.city = city;
        this.department = department;
    }

    public String getDepartment() {

        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String department;
}
