package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;


/**
 * Created by znickle on 10/10/18.
 */

public class BMRViewModel extends AndroidViewModel {
    private UserRepository mUserRepo;

    private LiveData<Integer> calorieIntake;
    public BMRViewModel(@NonNull Application application) {
        super(application);
        mUserRepo = new UserRepository(application);
    }
    public UserRepository getRepo(){ return mUserRepo; }
}
