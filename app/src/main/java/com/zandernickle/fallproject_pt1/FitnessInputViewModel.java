package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

public class FitnessInputViewModel extends AndroidViewModel {

    private UserRepository mRepository;

    public FitnessInputViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }

    public void updateUser(User user) {
        mRepository.updateUserAsync(user);
    }

}
