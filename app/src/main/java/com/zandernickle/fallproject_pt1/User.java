package com.zandernickle.fallproject_pt1;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.neovisionaries.i18n.CountryCode;


@Entity(tableName = "user_table",
        indices = {@Index(value = {"name"}, unique = true)})
public class User implements Parcelable {

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;
    @NonNull
    private byte[] profileImage;
    @NonNull
    private int age;
    @NonNull
    private int postalCode;
    @NonNull
    private CountryCode countryCode;

    private int BMI = -1;
    private int BMR = -1;
    private int calorieIntake = -1;
    private int height = -1;
    private int weight = -1;
    private int steps = 0;
    private Sex sex = null;
    private ActivityLevel activityLevel = null;

    private int weightGoal = -1; // neg, 0, or pos

    private User(Parcel in) {
        id = in.readInt();

        name = in.readString();
        in.readByteArray(profileImage);
        age = in.readInt();
        postalCode = in.readInt();
        countryCode = (CountryCode) in.readSerializable();

        height = in.readInt();
        weight = in.readInt();
        sex = (Sex) in.readSerializable();
        activityLevel = (ActivityLevel) in.readSerializable();
        weightGoal = in.readInt();
        BMR = in.readInt();
        calorieIntake = in.readInt();
        try {
            BMI = in.readInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        steps = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);

        dest.writeString(name);
        dest.writeByteArray(profileImage);
        dest.writeInt(age);
        dest.writeInt(postalCode);
        dest.writeSerializable(countryCode);

        dest.writeInt(height);
        dest.writeInt(weight);
        dest.writeSerializable(sex);
        dest.writeSerializable(activityLevel);
        dest.writeInt(weightGoal);
        dest.writeInt(BMR);
        dest.writeInt(calorieIntake);
        try {
            dest.writeInt(BMI);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dest.writeInt(steps);
    }

    public User() {

    }

    public User(String name, byte[] profileImage, int age, int postalCode, CountryCode countryCode) {
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
        this.countryCode = (CountryCode) signInBundle.getSerializable(Key.COUNTRY);
    }

    public void updateFitnessData(Bundle fitnessInputBundle) {
        this.height = fitnessInputBundle.getInt(Key.HEIGHT);
        this.weight = fitnessInputBundle.getInt(Key.WEIGHT);
        this.sex = (Sex) fitnessInputBundle.getSerializable(Key.SEX);
        this.activityLevel = (ActivityLevel) fitnessInputBundle.getSerializable(Key.ACTIVITY_LEVEL);
        this.weightGoal = fitnessInputBundle.getInt(Key.GOAL);
    }

    public void updateHealthData(Bundle BMRBundle) {

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

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(CountryCode countryCode) {
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

    public int getBMI() { return BMI; }

    public void setBMI(int bmi) { this.BMI = bmi; }

    public int getBMR() { return BMR; }

    public void setBMR(int bmr) { this.BMR = bmr; }

    public int getCalorieIntake() { return calorieIntake; }

    public void setCalorieIntake(int calorieIntake) { this.calorieIntake = calorieIntake; }

    public void setProfileImage(@NonNull byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public void setWeightGoal(int weightGoal) {
        this.weightGoal = weightGoal;
    }

    public int getSteps() { return steps; }

    public void setSteps(int step) { this.steps = step; }
}
