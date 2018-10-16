package com.zandernickle.fallproject_pt1;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;


/**
 * This class represents a table in the Room database containing the id of the currently signed-in ("active") user. Use
 * this class to ensure the correct user is signed in on app start-up.
 */
@Entity(tableName = "current_user_table",
        foreignKeys = {@ForeignKey(entity = User.class, parentColumns = {"id"}, childColumns = {"id"},
                onUpdate = CASCADE, onDelete = CASCADE)})
public class ActiveUser {

    @NonNull
    @PrimaryKey
    private int key = 0;
    @NonNull
    private int id;

    ActiveUser() {
        // This constructor is required for database access.
    }

    @NonNull
    public int getKey() {
        return key;
    }

    public void setKey(@NonNull int key) {
        this.key = key;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }
}
