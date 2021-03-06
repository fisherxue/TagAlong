package com.tagalong.tagalong.models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data Structure Profile - user profile
 */
public class Profile implements Serializable {
    // Restrictions
    public static final int minAgeDriver = 19;
    public static final int minAgeRider = 15;
    public static final int maxAge = 100;
    public static final int minCarCapacity = 1;
    public static final int maxCarCapacity = 10;

    private String userID;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String fbToken;
    private Boolean isDriver;
    private int age;
    private String joinedDate;
    private int carCapacity;
    private int[] interests;

    // Default profile constructor
    public Profile() {
        this.userID = "Not Set";
        this.firstName = "Not Set";
        this.lastName = "Not Set";
        this.username = "Not Set";
        this.password = "Not Set";
        this.email = "Not Set";
        this.gender = "Not Set";
        this.isDriver = true;
        this.age = 0;
        this.carCapacity = 0;
        this.interests = new int[5];
        for (int i = 0; i < interests.length; i++){
            interests[i] = 2;
        }
        this.joinedDate = "Not Set";
    }


    // Getters
    public String getUserID() {
        return userID;
    }

    public String getJoinedDate() {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
                    .parse(this.joinedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM YYYY, hh:mm:ss");
        return dateFormat.format(date);
    }

    public String getFirstName() {
        return firstName;
    }

    public int[] getInterests() {
        return interests;
    }

    public int getCarCapacity() {
        return carCapacity;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
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

    //Setters
    public void setInterests(int[] interests) {
        this.interests = interests;
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

    public void setUsername(String username) {
        this.username = username;
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

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

}
