package com.example.tagalong;

import java.io.Serializable;
import java.sql.Date;

public class Profile implements Serializable {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private Boolean isDriver;
    private String gender;
    private int age;
    private String interests;
    private int carCapacity;
    private String _id;
    private String joinedDate;

    public String getFirstName() {
        return firstName;
    }

    public int getCarCapacity() {
        return carCapacity;
    }

    public void setCarCapacity(int carCapacity) {
        this.carCapacity = carCapacity;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDriver(Boolean driver) {
        isDriver = driver;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setInterest(String interests) {
        this.interests = interests;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getDriver() {
        return isDriver;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getInterest() {
        return interests;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String get_id() {
        return _id;
    }

    public String getJoinedDate() {
        return joinedDate;
    }
}
