package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;


public class HikesViewModel extends AndroidViewModel {

    private MutableLiveData<WeatherData> jsonData;

    //Variable used for the Hikes Repository
    private HikesRepository mHikesRepository;

    public HikesViewModel(Application application){
        super(application);
        mHikesRepository = new HikesRepository(application);
        jsonData = mHikesRepository.getData();
    }

    //Pass location string to the Hikes Repository
    public void setLocation(String location){
        mHikesRepository.setLocation(location);
    }

    public LiveData<WeatherData> getData(){
        return jsonData;
    }

}
