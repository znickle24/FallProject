package com.zandernickle.fallproject_pt1;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.neovisionaries.i18n.CountryCode;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.zandernickle.fallproject_pt1.ReusableUtil.loadMenuBarFragment;


public class HikesFragment extends Fragment implements View.OnClickListener, LocationListener{

    //Create variables TextViews that hold the current weather data
    private TextView mTvTemp, mTvHighTemp, mTvLowTemp, mTvPrecip;

    //Variable for the "Search for Hikes" button
    private Button mButtonSearch;

    //Variables that hold the current location's latitude and longitude
    private double mLatitude, mLongitude;

    //Variables holding city name and country name Strings
    private String mCityName, mCountryName;

    //Variable holding Hikes ViewModel
    private HikesViewModel mHikesViewModel;

    //Used to find current location
    protected LocationManager locationManager;

    //Used to determine units used in weather data displayed
    private Bundle mArgsReceived;
    private User mUser;
    private boolean mAmerican = false;

    private Context mContext;


    //HikesFragment constructor
    public HikesFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the HikesFragment layout view
        View view = inflater.inflate(R.layout.fragment_hikes, container, false);

        //Get hike search button and set it to an onClickListener
        mButtonSearch = (Button) view.findViewById(R.id.btn_search_hikes);
        mButtonSearch.setOnClickListener(this);

        //Get the TextViews to display the temperature and precipitation
        mTvTemp = (TextView) view.findViewById(R.id.tv_current_temp_data);
        mTvHighTemp = (TextView) view.findViewById(R.id.tv_high_temp_data);
        mTvLowTemp = (TextView) view.findViewById(R.id.tv_low_temp_data);
        mTvPrecip = (TextView) view.findViewById(R.id.tv_precipitation_data);

        getLatLong(); //get current latitude and longitude

        //Get the current city name and country name for display
        //String currentCityCountry = getCityAndCountry(mLatitude, mLongitude); //NOT WORKING BECAUSE OF GEOCODER
        //Log.d("currentCityCountry in HikesFragment.java: ", currentCityCountry);

        //Extract any pertinent data here from SignIn Activity
        mArgsReceived = getArguments();
        mUser = mArgsReceived.getParcelable(Key.USER);
        //mCityName = mArgsReceived.getString(Key.CITY); //NEED TO CHANGE KEY???
        mCountryName = mArgsReceived.getString(Key.COUNTRY);
//        String currentCityCountry = mCityName + "&" + mCountryName;

        //Create the Hikes ViewModel
        mHikesViewModel = ViewModelProviders.of(this).get(HikesViewModel.class);

        //Set the observer
        mHikesViewModel.getData().observe(this,nameObserver);

        String currentCityCountry = "Salt Lake City&US";
        loadWeatherData(currentCityCountry); //used to get current weather info

        if(mArgsReceived.get(Key.COUNTRY) == CountryCode.US) {
            mAmerican = true; //determines which units are displayed with weather data
        }

        loadMenuBarFragment(HikesFragment.this, mArgsReceived, R.id.fl_menu_bar_fragment_placeholder);

        return view;
    }


    //Set mLatitude and mLongitude
    public void getLatLong() {

        //Check permissions
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location location;

            if (network_enabled) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    mLatitude = location.getLatitude();
                    Log.d("mLatitude in getLatLong(): ", Double.toString(mLatitude));
                    mLongitude = location.getLongitude();
                    Log.d("mLongitude in getLatLong(): ", Double.toString(mLongitude));
                }
            }
        }

    }


    /**
     * Handles clicks for the hike search button
     * Finds the user's current location
     * Presents the user with hikes near their current location using Google Maps
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search_hikes: {
                if(mLatitude == 0.0 && mLongitude == 0.0) {
                    mLatitude = 40.7678;
                    mLongitude = -111.8452;
                }

                Uri searchUri = Uri.parse("geo:" + Double.toString(mLatitude) + "," + Double.toString(mLongitude) + "?q=" + "hikes");
                Log.d("searchUri string: ", searchUri.toString());

                //Create the implicit intent for Google Maps
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, searchUri);

                //If there's an activity associated with this intent, launch it
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }

        }
    } //end of onClick function


    void loadWeatherData(String location){
        //pass the location in to the view model
        mHikesViewModel.setLocation(location);
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
            Log.d("HERE: ", "HERE");
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
        Log.d("mCityName in getCityAndCountry: ", mCityName);
        mCountryName = countryName;
        Log.d("mCountryName in getCityAndCountry: ", mCountryName);

        //Create city name and country name String
        cityCountryString = cityName + "&" + countryName;
        return cityCountryString;
    }


    //create an observer that watches the LiveData<WeatherData> object
    final Observer<WeatherData> nameObserver  = new Observer<WeatherData>() {
        @Override
        public void onChanged(@Nullable final WeatherData weatherData) {
            //Update the UI if this data variable changes
            //Set location data and weather info to TextViews
            //HikesFragment shows current temp, high temp, low temp, and precipitation
            if (weatherData != null) {
                double precipAmount = weatherData.getRain().getAmount() + weatherData.getSnow().getAmount();
                Log.d("precipAmount: ", Double.toString(precipAmount));
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
            }

        }
    };


    @Override
    public void onLocationChanged(Location location) {
        Log.d("HikesFragment.java: ", "in onLocationChanged() in HikesFragment.java");
        mLatitude = location.getLatitude();
        Log.d("mLatitude: ", Double.toString(mLatitude));
        mLongitude = location.getLongitude();
        Log.d("mLongitude: ", Double.toString(mLongitude));
    }


    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }


    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

}