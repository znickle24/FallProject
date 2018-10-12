package com.zandernickle.fallproject_pt1;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    // TODO
    @Query("SELECT COUNT(id) FROM user_table")
    int getCount();

    @Query("SELECT * FROM user_table WHERE id = :userId")
    LiveData<User> getUser(int userId);

}
