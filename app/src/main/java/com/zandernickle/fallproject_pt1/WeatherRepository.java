package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;


public class WeatherRepository {

    private final MutableLiveData<WeatherData> jsonData = new MutableLiveData<>();

    //Variable holding the location for the weather
    private String mLocation;

    WeatherRepository(Application application){
        loadData();
    }


    //Sets the location to get the weather
    public void setLocation(String location){
        mLocation = location;
        loadData();
    }

    public MutableLiveData<WeatherData> getData() {
        return jsonData;
    }

    private void loadData(){
        new AsyncTask<String,Void,String>(){
            @Override
            protected String doInBackground(String... strings) {
                String location = strings[0];
                URL weatherDataURL = null;
                String retrievedJsonData = null;
                if(location!=null) {
                    weatherDataURL = Network.buildURLFromString(location);
                    try {
                        retrievedJsonData = Network.getDataFromURL(weatherDataURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return retrievedJsonData;
            }

            @Override
            protected void onPostExecute(String s) {
                if(s != null) {
                    try {
                        jsonData.setValue(JSONWeather.getWeatherData(s));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.execute(mLocation);
    }

}
