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

    public SignInViewModel(@NonNull Application application) {
        super(application);
        // TODO: Add repo for accessing database when user hits submit.
    }

}
