package com.zandernickle.fallproject_pt1;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Query("UPDATE user_table SET sex = :sex, activityLevel = :activityLevel, height = :height, weight = :weight, weightGoal = :delta WHERE id = :id")
    public void updateUserFitness(int id, Sex sex, ActivityLevel activityLevel, int height, int weight, int delta);

    @Query("UPDATE user_table SET BMI = :BMI, BMR = :BMR, calorieIntake = :calorieIntake WHERE id = :id")
    public void updateUserHealth(int id, int BMI, int BMR, int calorieIntake);

    @Query("SELECT COUNT(id) FROM user_table")
    LiveData<Integer> getCount();

    @Query("SELECT * FROM user_table")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM user_table WHERE id = :id")
    LiveData<User> getUser(int id);

}
