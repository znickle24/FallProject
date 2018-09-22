package com.zandernickle.fallproject_pt1;

import android.os.Bundle;

public class User {

    private int id;
    private String name;
    private byte[] profileImage;

    private int age;
    private int postalCode;
    private String countryCode;

    private int height;
    private int weight;
    private Sex sex;
    private ActivityLevel activityLevel;
    private int weightGoal; // neg, 0, or pos

    public User() {

    }

    public User(String name, byte[] profileImage, int age, int postalCode, String countryCode) {
        this.name = name;
        this.profileImage = profileImage;
        this.age = age;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
    }

    public User(Bundle signInBundle) {
        this.name = signInBundle.getString(Key.NAME);
        this.profileImage = signInBundle.getByteArray(Key.PROFILE_IMAGE);
        this.age = signInBundle.getInt(Key.AGE);
        this.postalCode = signInBundle.getInt(Key.POSTAL_CODE);
        this.countryCode = signInBundle.getString(Key.COUNTRY);
    }

    public void updateFitnessData(Bundle fitnessInputBundle) {
        this.height = fitnessInputBundle.getInt(Key.HEIGHT);
        this.weight = fitnessInputBundle.getInt(Key.WEIGHT);
        this.sex = (Sex) fitnessInputBundle.getSerializable(Key.SEX);
        this.activityLevel = (ActivityLevel) fitnessInputBundle.getSerializable(Key.ACTIVITY_LEVEL);
        this.weightGoal = fitnessInputBundle.getInt(Key.GOAL);
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

    public byte[] getProfileImage() {
        return profileImage;
    }

    public int getWeightGoal() {
        return weightGoal;
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
