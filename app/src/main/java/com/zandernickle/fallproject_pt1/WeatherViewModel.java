package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;


public class WeatherViewModel extends AndroidViewModel {
    private MutableLiveData<WeatherData> jsonData;
    private WeatherRepository mWeatherRepository;

    public WeatherViewModel(Application application){
        super(application);
        Log.d("In WeatherViewModel constructor in WeatherViewModel.java", "");
        mWeatherRepository = new WeatherRepository(application);
        jsonData = mWeatherRepository.getData();
    }
    public void setLocation(String location){
        Log.d("In setLocation in WeatherViewModel.java", "");
        Log.d("location in setLocation in WeatherViewModel.java: ", location);
        mWeatherRepository.setLocation(location);
    }

    public LiveData<WeatherData> getData(){
        Log.d("In getData in WeatherViewModel.java", "");
        return jsonData;
    }


}
