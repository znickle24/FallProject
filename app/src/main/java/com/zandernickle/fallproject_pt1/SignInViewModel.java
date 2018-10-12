package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.neovisionaries.i18n.CountryCode;

public class SignInViewModel extends AndroidViewModel {

    private UserRepository mRepository;
    private LiveData<User> mUser;
    private LiveData<Integer> count;

    public SignInViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }

    LiveData<User> getUser() { return mUser; }

    public void insert(User user) { mRepository.insert(user); }



    /* Temporary data to be saved on configuration change.
     * No User instance will be created until the user submits the data.
     */
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<Integer> ageIndex = new MutableLiveData<>();
    private MutableLiveData<String> postalCode = new MutableLiveData<>();
    private MutableLiveData<Integer> countryCodeIndex = new MutableLiveData<>();

    public MutableLiveData<String> getName() {
        return name;
    }

    public MutableLiveData<String> getPostalCode() {
        return postalCode;
    }

    public MutableLiveData<Integer> getAgeIndex() {
        return ageIndex;
    }

    public MutableLiveData<Integer> getCountryCodeIndex() {
        return countryCodeIndex;
    }

}

