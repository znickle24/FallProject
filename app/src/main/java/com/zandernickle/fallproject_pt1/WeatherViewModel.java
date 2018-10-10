package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;


public class WeatherViewModel extends AndroidViewModel {

    private MutableLiveData<WeatherData> jsonData;

    //Variable used for the Weather Repository
    private WeatherRepository mWeatherRepository;

    public WeatherViewModel(Application application){
        super(application);
        mWeatherRepository = new WeatherRepository(application);
        jsonData = mWeatherRepository.getData();
    }

    //Pass location string to the Weather Repository
    public void setLocation(String location){
        mWeatherRepository.setLocation(location);
    }

    public LiveData<WeatherData> getData(){
        return jsonData;
    }

}
