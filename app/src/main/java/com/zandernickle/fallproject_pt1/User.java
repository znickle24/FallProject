package com.zandernickle.fallproject_pt1;

import android.os.Bundle;

public class User {

    private int id;
    private String name;
    private int age;
    private int postalCode;
    private String countryCode;

    private int height;
    private int weight;
    private Sex sex;
    private ActivityLevel activityLevel;
    // TODO: Add objective (weight loss/gain per week).

    public User() {

    }

    public User(String name, int age, int postalCode, String countryCode) {
        this.name = name;
        this.age = age;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
    }

    public User(Bundle signInBundle) {
        this.name = signInBundle.getString(Key.NAME);
        this.age = signInBundle.getInt(Key.AGE);
        this.postalCode = signInBundle.getInt(Key.POSTAL_CODE);
        this.countryCode = signInBundle.getString(Key.COUNTRY);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }
}
