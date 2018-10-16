package com.zandernickle.fallproject_pt1;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.ABORT;
import static android.arch.persistence.room.OnConflictStrategy.FAIL;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import static android.arch.persistence.room.OnConflictStrategy.ROLLBACK;

@Dao
public interface UserDao {

    /**
     * Adds a new {@link User}, u to the database and returns the assigned id. If the User cannot be added to the
     * database, returns {@link android.arch.persistence.room.OnConflictStrategy#ABORT}.
     *
     * TODO
     * This method is throwing an Exception before returning OnConflictStrategy.ABORT. See this method call in
     * UserRepository.java for more information.
     *
     * @param u User to be added to the database.
     * @return the id of u, having been appended to the database, or OnConflictStrategy.ABORT if not.
     */
    @Insert
    long insert(User u);

//    /**
//     * Adds a new {@link ActiveUser}, a to the database if the corresponding table does NOT contain ANY entries. If the
//     * table does contain an entry, that entry is updated.
//     *
//     * Important! The ActiveUser table is intended to contain only a single row. Once created, the entry should only be
//     * updated. This is currently enforced by {@link UserRepository#update(ActiveUser)}.
//     *
//     * TODO
//     * Possible to prevent the getter/setter for the key to be used outside of the database?
//     *
//     * @param a ActiveUser to be added to or updated in the database.
//     */
    @Insert(onConflict = REPLACE)
    void insert(ActiveUser a);

    @Query("SELECT * FROM ActiveUser WHERE `rowId` = :rowId" )
    ActiveUser getActiveUser(int rowId);

    @Query("SELECT * FROM user_table WHERE id = :id")
    User getUserSync(int id);

    @Query("SELECT * FROM user_table WHERE id = :id")
    LiveData<User> getUserAsync(int id);

    @Update
    void updateUser(User user);


//    @Query("UPDATE user_table SET sex = :sex, activityLevel = :activityLevel, height = :height, weight = :weight, weightGoal = :delta WHERE id = :id")
//    void updateUserFitness(int id, Sex sex, ActivityLevel activityLevel, int height, int weight, int delta);
//
//    @Query("UPDATE user_table SET BMI = :BMI, BMR = :BMR, calorieIntake = :calorieIntake WHERE id = :id")
//    void updateUserHealth(int id, int BMI, int BMR, int calorieIntake);

    @Query("SELECT COUNT(id) FROM user_table")
    LiveData<Integer> getCount();

//    @Query("SELECT * FROM user_table")
//    LiveData<List<User>> getAllUsers();



}
