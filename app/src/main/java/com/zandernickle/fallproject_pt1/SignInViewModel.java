package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.neovisionaries.i18n.CountryCode;

import java.util.List;

public class SignInViewModel extends AndroidViewModel {

    private UserRepository mRepository;
    private LiveData<Integer> count;
    private LiveData<List<User>> allUsers;
    private LiveData<User> mUser;

    public SignInViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }

    LiveData<Integer> getCount() { return mRepository.getCount(); }
    LiveData<List<User>> getAllUsers() { return mRepository.getAllUsers(); }
    LiveData<User> getUser(int id) { return mRepository.getUser(id); }

    public long addUser(User user) { return mRepository.addUser(user); }

    public void updateUserFitness(User user) { mRepository.updateUserFitness(user); }
    public void updateUserHealth(User user) { mRepository.updateUserHealth(user); }




    // TODO

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

