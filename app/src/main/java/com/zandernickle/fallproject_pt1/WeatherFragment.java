package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.neovisionaries.i18n.CountryCode;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static com.zandernickle.fallproject_pt1.ReusableUtil.loadMenuBarFragment;


public class WeatherFragment extends Fragment {

    //Variables for TextViews displaying location and weather data
    private TextView mTvLocation, mTvTemp, mTvHighTemp, mTvLowTemp,
            mTvPrecip, mTvPressure, mTvHumid;

    //Variables holding city name and country name Strings
    private String mCityName, mCountryName;

    //Variable holding weather data for display
    private WeatherData mWeatherData;

    //Variables used to determine which units to use when displaying weather data
    private Bundle mArgsReceived;
    private boolean mAmerican = false;

    private Context mContext;


    //WeatherFragment Constructor
    public WeatherFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the HikesFragment layout view
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        //Get the TextViews to display location and weather data
        mTvLocation = (TextView) view.findViewById(R.id.tv_location_data);
        mTvTemp = (TextView) view.findViewById(R.id.tv_current_temp_data);
        mTvHighTemp = (TextView) view.findViewById(R.id.tv_high_temp_data);
        mTvLowTemp = (TextView) view.findViewById(R.id.tv_low_temp_data);
        mTvPrecip = (TextView) view.findViewById(R.id.tv_precipitation_data);
        mTvPressure = (TextView) view.findViewById(R.id.tv_pressure_data);
        mTvHumid = (TextView) view.findViewById(R.id.tv_humidity_data);

        //Get the current city name and country name for display
        //HikesFragment.getLatLong();
        //String currentCityCountry = getCityAndCountry(mLatitude, mLongitude); //NOT WORKING BECAUSE OF GEOCODER

        //Extract any pertinent data here from SignInActivity
        mArgsReceived = getArguments();
//        mCityName = mArgsReceived.getString("CITY"); //NEED TO CHANGE KEY???
//        mCountryName = mArgsReceived.getString("COUNTRY"); //NEED TO CHANGE KEY???
//        String currentCityCountry = mCityName + "&" + mCountryName;

        String currentCityCountry = "Salt Lake City&US";
        //mTvLocation.setText("" + getCityName() + ", " + getCountryName());
        //mTvLocation.setText("" + mCityName + ", " + mCountryName);
        mTvLocation.setText("" + "Salt Lake City" + ", " + "US");
        loadWeatherData(currentCityCountry); //used to get current weather info

        if(mArgsReceived.get(Key.COUNTRY) == CountryCode.US) {
            mAmerican = true; //determines which units are displayed with weather data
        }

        loadMenuBarFragment(WeatherFragment.this, mArgsReceived, R.id.fl_menu_bar_fragment_placeholder);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }


    public String getCityName(){
        return this.mCityName;
    }


    public String getCountryName(){
        return this.mCountryName;
    }


    //Used to get current weather info
    private void loadWeatherData(String location) {
        new FetchWeatherTask().execute(location);
    }


    /**
     * NOT WORKING BECAUSE OF GEOCODER
     * Returns a city name and country name String used to find weather info
     * @param latitude
     * @param longitude
     * @return String that includes the city name and country name
     */
    private String getCityAndCountry(double latitude, double longitude) {
        String cityCountryString = "";
        String cityName = "";
        String countryName = "";

        Geocoder geoCoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                cityName = addresses.get(0).getLocality().toString(); //get city name
                countryName = addresses.get(0).getCountryName().toString(); //get country name
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //Set member variable city name and country name variables
        mCityName = cityName;
        mCountryName = countryName;

        //Create city name and country name String
        cityCountryString = cityName + "&" + countryName;
        return cityCountryString;
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... inputStringArray) {
            String location = inputStringArray[0];
            URL weatherDataURL = Network.buildURLFromString(location);
            String jsonWeatherData = null;
            try {
                jsonWeatherData = Network.getDataFromURL(weatherDataURL);
                return jsonWeatherData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonWeatherData) {
            if (jsonWeatherData != null) {
                try {
                    mWeatherData = JSONWeather.getWeatherData(jsonWeatherData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Set weather info to TextViews
                if (mWeatherData != null) {
                    double precipAmount = mWeatherData.getRain().getAmount() + mWeatherData.getSnow().getAmount();
                    Log.d("precipAmount: ", Double.toString(precipAmount));
                    if(mAmerican) {
                        mTvTemp.setText("" + Math.round((9/5)*(mWeatherData.getTemperature().getTemp() - 273.15) + 32) + " F");
                        mTvHighTemp.setText("" + Math.round((9/5)*(mWeatherData.getTemperature().getMaxTemp() - 273.15) + 32) + " F");
                        mTvLowTemp.setText("" + Math.round((9/5)*(mWeatherData.getTemperature().getMinTemp() - 273.15) + 32) + " F");
                        mTvPrecip.setText("" +  precipAmount + " in");
                    } else {
                        mTvTemp.setText("" + Math.round(mWeatherData.getTemperature().getTemp() - 273.15) + " C");
                        mTvHighTemp.setText("" + Math.round(mWeatherData.getTemperature().getMaxTemp() - 273.15) + " C");
                        mTvLowTemp.setText("" + Math.round(mWeatherData.getTemperature().getMinTemp() - 273.15) + " C");
                        mTvPrecip.setText("" +  precipAmount*25.4 + " mm");
                    }
                    mTvPressure.setText("" + mWeatherData.getCurrentCondition().getPressure() + " hPa");
                    mTvHumid.setText("" + mWeatherData.getCurrentCondition().getHumidity() + "%");
                }
            }
        }
    }

}
