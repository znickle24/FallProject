package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;


public class SignInViewModel extends AndroidViewModel {

    public String name;
    public String postalCode;
    public byte[] profileImage;
    public int ageSpinPosition = 0;
    public int countrySpinPosition = 0;

    private UserRepository mRepository;

    public SignInViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }

    public int addUserSync(User user) {
        return mRepository.addUserSync(user);
    }

    public void updateActiveUser(int id) {
        mRepository.updateActiveUser(id);
    }

    public int getActiveUser() {
        return mRepository.getActiveUserId();
    }

}
