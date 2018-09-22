package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class WeatherFragment extends Fragment implements View.OnClickListener{

    //Variables for TextViews displaying location and weather data
    private TextView mTvLocation, mTvTemp, mTvHighTemp, mTvLowTemp,
            mTvPrecip, mTvPressure, mTvHumid;

    //Variables for other UI elements (Master List and Profile Pic)
    private Spinner mSpinner;
    private ImageView mIvProfilePic;

    //Variables holding current latitude and longitude
    private double mLatitude, mLongitude;

    //Variables holding city name and country name Strings
    private String mCityName, mCountryName;

    private WeatherData mWeatherData;
    private Context mContext;

    OnDataPass mDataPasser;


    //WeatherFragment Constructor
    public WeatherFragment() {
    }


    //Callback interface
    public interface OnDataPass{
        public void onDataPass(String data);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the HikesFragment layout view
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        //Set Spinner (Master List) to an onClickListener
        mSpinner = (Spinner) view.findViewById(R.id.spinner);
        mSpinner.setOnClickListener(this);

        //Get the ImageView to set the profile pic in
        mIvProfilePic = (ImageView) view.findViewById(R.id.iv_pic);

        //Get the TextViews to display location and weather data
        mTvLocation = (TextView) view.findViewById(R.id.tv_location_data);
        mTvTemp = (TextView) view.findViewById(R.id.tv_temp_data);
        mTvHighTemp = (TextView) view.findViewById(R.id.tv_high_temp_data);
        mTvLowTemp = (TextView) view.findViewById(R.id.tv_low_temp_data);
        mTvPrecip = (TextView) view.findViewById(R.id.tv_precip_data);
        mTvPressure = (TextView) view.findViewById(R.id.tv_pressure_data);
        mTvHumid = (TextView) view.findViewById(R.id.tv_humid_data);

        //Get the current latitude, longitude, city name, and country name
        mLatitude = GPSTracker.getLatitude();
        mLongitude = GPSTracker.getLongitude();
        String currentCityCountry = getCityAndCountry(mLatitude, mLongitude);
        loadWeatherData(currentCityCountry); //used to get current weather info

        //Set the ImageView with the profile pic
        Bundle picBundle = getArguments();
        Bitmap thumbnailImage = ReusableUtil.bitmapFromBundle(picBundle, Key.PROFILE_IMAGE);
        if (thumbnailImage != null) {
            mIvProfilePic.setImageBitmap(thumbnailImage);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

        try{
            mDataPasser = (OnDataPass) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnDataPass");
        }
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
                //Set location data and weather info to TextViews
                if (mWeatherData != null) {
                    mTvLocation.setText("" + getCityName() + ", " + getCountryName());
                    mTvTemp.setText("" + Math.round(mWeatherData.getTemperature().getTemp() - 273.15) + " C");
                    mTvHighTemp.setText("" + Math.round(mWeatherData.getTemperature().getMaxTemp() - 273.15) + " C");
                    mTvLowTemp.setText("" + Math.round(mWeatherData.getTemperature().getMinTemp() - 273.15) + " C");

                    //WILL HAVE TO DO MORE PROCESSING OF RAIN AND SNOW FOR PRECIPITATION POSSIBLY
                    double precipAmount = mWeatherData.getRain().getAmount() + mWeatherData.getSnow().getAmount();
                    mTvPrecip.setText("" +  precipAmount + "in"); //MAY NEED TO CHANGE UNITS

                    mTvPressure.setText("" + mWeatherData.getCurrentCondition().getPressure() + " hPa");
                    mTvHumid.setText("" + mWeatherData.getCurrentCondition().getHumidity() + "%");
                }
            }
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.spinner: {
                mDataPasser.onDataPass(Key.WEATHER_FRAGMENT);
            }
            break;
        }
    }

}
