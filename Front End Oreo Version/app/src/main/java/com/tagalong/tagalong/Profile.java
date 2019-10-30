package com.tagalong.tagalong;

import java.io.Serializable;

public class Profile implements Serializable {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private Boolean isDriver;
    private String gender;
    private int age;
    //private String interests;
    private int carCapacity;
    private String userID;
    private String joinedDate;

    public Profile() {
        this.firstName = "Not Set";
        this.lastName = "Not Set";
        this.username = "Not Set";
        this.password = "Not Set";
        this.email = "Not Set";
        this.isDriver = false;
        this.gender = "Not Set";
        this.age = 0;
        this.carCapacity = 0;
        this.userID = "Not Set";
        this.joinedDate = "Not Set";
        this.interests = new int[5];
    }


    public int[] getInterests() {
        return interests;
    }

    public void setInterests(int[] interests) {
        this.interests = interests;
    }

    private int[] interests;

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

    /*public void setInterest(String interests) {
        this.interests = interests;
    }*/

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

    /*public String getInterest() {
        return interests;
    }*/

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getUserID() {
        return userID;
    }

    public String getJoinedDate() {
        return joinedDate;
    }
}
