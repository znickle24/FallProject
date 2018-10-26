package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.neovisionaries.i18n.CountryCode;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import static com.zandernickle.fallproject_pt1.ReusableUtil.loadMenuBarFragment;


public class WeatherFragment extends Fragment {

    //Variables for TextViews displaying location and weather data
    private TextView mTvLocation, mTvTemp, mTvHighTemp, mTvLowTemp,
            mTvPrecip, mTvPressure, mTvHumid;

    //Variables holding city name and country name Strings
    private String mCityName, mCountryName;

    //Variable holding Weather ViewModel
    private WeatherViewModel mWeatherViewModel;

    //Variables used to determine which units to use when displaying weather data
    private Bundle mArgsReceived;
    private User mUser;
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
        //Inflate the WeatherFragment layout view
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
        mUser = mArgsReceived.getParcelable(Key.USER);
        //mCityName = mArgsReceived.getString(Key.CITY); //NEED TO CHANGE KEY???
        mCountryName = mArgsReceived.getString(Key.COUNTRY);
//        String currentCityCountry = mCityName + "&" + mCountryName;

        //Create the view model
        mWeatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);

        //Set the observer
        mWeatherViewModel.getData().observe(this,nameObserver);

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


    void loadWeatherData(String location){
        //pass the location in to the view model
        mWeatherViewModel.setLocation(location);
    }


    /**
     * NOT WORKING BECAUSE OF GEOCODER
     * Returns a city name and country name String used to find weather info
     * @param latitude
     * @param longitude
     * @return String that includes the city name and country name
     */
    public String getCityAndCountry(double latitude, double longitude) {
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


    //create an observer that watches the LiveData<WeatherData> object
    final Observer<WeatherData> nameObserver  = new Observer<WeatherData>() {
        @Override
        public void onChanged(@Nullable final WeatherData weatherData) {
            // Update the UI if this data variable changes
            //Set weather info to TextViews
                if (weatherData != null) {
                    double precipAmount = weatherData.getRain().getAmount() + weatherData.getSnow().getAmount();
                    double converter = 9.0/5.0;
                    if(mAmerican) {
                        mTvTemp.setText("" + Math.round(((converter)*(weatherData.getTemperature().getTemp() - 273.15)) + 32.0) + " F");
                        mTvHighTemp.setText("" + Math.round(((converter)*(weatherData.getTemperature().getMaxTemp() - 273.15)) + 32.0) + " F");
                        mTvLowTemp.setText("" + Math.round(((converter)*(weatherData.getTemperature().getMinTemp() - 273.15)) + 32.0) + " F");
                        mTvPrecip.setText("" +  precipAmount + " in");
                    } else {
                        mTvTemp.setText("" + Math.round(weatherData.getTemperature().getTemp() - 273.15) + " C");
                        mTvHighTemp.setText("" + Math.round(weatherData.getTemperature().getMaxTemp() - 273.15) + " C");
                        mTvLowTemp.setText("" + Math.round(weatherData.getTemperature().getMinTemp() - 273.15) + " C");
                        mTvPrecip.setText("" +  precipAmount*25.4 + " mm");
                    }
                    mTvPressure.setText("" + weatherData.getCurrentCondition().getPressure() + " hPa");
                    mTvHumid.setText("" + weatherData.getCurrentCondition().getHumidity() + "%");
                }

        }
    };

}
