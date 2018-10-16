package com.zandernickle.fallproject_pt1;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;


/**
 * This class represents a table in the Room database containing the userId of the currently signed-in ("active") user. Use
 * this class to ensure the correct user is signed in on app start-up.
 */
@Entity(foreignKeys = {@ForeignKey(entity = User.class, parentColumns = {"id"}, childColumns = {"userId"},
        onUpdate = CASCADE, onDelete = CASCADE)})
public class ActiveUser {

    @NonNull
    @PrimaryKey
    private int rowId;
    @NonNull
    private int userId;

    ActiveUser() {
        // This constructor is required for database access.
    }

    @NonNull
    public int getRowId() {
        return rowId;
    }

    public void setRowId(@NonNull int rowId) {
        this.rowId = rowId;
    }

    @NonNull
    public int getUserId() {
        return userId;
    }

    public void setUserId(@NonNull int userId) {
        this.userId = userId;
    }
}
