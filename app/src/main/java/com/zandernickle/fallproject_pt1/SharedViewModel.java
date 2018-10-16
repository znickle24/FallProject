package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

public class SharedViewModel extends AndroidViewModel {

    private LiveData<User> mUser;
    private UserRepository mRepository;

    public SharedViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }

    public LiveData<User> getUser() {
        return mUser;
    }

}
