package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.service.autofill.UserData;

import java.util.List;

/**
 * Created by znickle on 10/10/18.
 */

public class BMRRepository {
    private LiveData<BMRData> bmrData;

    private int BMR;
    private int BMI;
    private UserDao dao;
    private LiveData<List<User>> users;
    BMRRepository(Application application) {
        UserDatabase db = UserDatabase.getDatabase(application);
        dao = db.userDao();
        users = dao.getAllUsers();
    }



}
