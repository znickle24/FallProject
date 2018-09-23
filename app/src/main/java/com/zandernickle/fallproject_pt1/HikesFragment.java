package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static com.zandernickle.fallproject_pt1.ReusableUtil.loadMenuBarFragment;


public class HikesFragment extends Fragment implements View.OnClickListener, LocationListener{

    //Create variables TextViews that hold the current weather data
    private TextView mTvTemp, mTvHighTemp, mTvLowTemp, mTvPrecip;

    //Variables for the other UI elements
    private Button mButtonSearch;

    //Variables that hold the current location's latitude and longitude
    private double mLatitude, mLongitude;

    //Variables holding city name and country name Strings
    private String mCityName, mCountryName;

    private WeatherData mWeatherData;
    Context mContext;

    //Used to find current location
    protected LocationManager locationManager;

    /**
     * Constructor for HikesFragment
     */
    public HikesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //checkPermission();
        //setRetainInstance(true); //ORIGINAL
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context; //added; MIGHT NOT NEED
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the HikesFragment layout view
        View view = inflater.inflate(R.layout.fragment_hikes, container, false);

        //Get hike search button and set it to an onClickListener
        mButtonSearch = (Button) view.findViewById(R.id.btn_search_hikes);
        mButtonSearch.setOnClickListener(this);

        //Get the TextViews to display the temperature and humidity
        mTvTemp = (TextView) view.findViewById(R.id.tv_current_temp_data);
        mTvHighTemp = (TextView) view.findViewById(R.id.tv_high_temp_data);
        mTvLowTemp = (TextView) view.findViewById(R.id.tv_low_temp_data);
        mTvPrecip = (TextView) view.findViewById(R.id.tv_precipitation_data);


        getLatLong();

//        //Get the current latitude, longitude, city name, and country name for display
//        mLatitude = GPSTracker.getLatitude();
//        mLongitude = GPSTracker.getLongitude();
        //String currentCityCountry = getCityAndCountry(mLatitude, mLongitude);
        String currentCityCountry = "Sandy&USA";
        loadWeatherData(currentCityCountry); //used to get current weather info

        Bundle arguments = getArguments();
        // Extract any pertinent data here, then pass to the menu bar (profile image and module name)

        loadMenuBarFragment(HikesFragment.this, arguments, R.id.fl_menu_bar_fragment_placeholder);

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void getLatLong(){

        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location;

        if (network_enabled) {

            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                mLatitude = location.getLatitude();
                Log.d("mLatitude: ", Double.toString(mLatitude));
                mLongitude = location.getLongitude();
                Log.d("mLongitude: ", Double.toString(mLongitude));

            }
        }

    }


    /**
     * Handles clicks for the hike search button
     * Finds the user's current location
     * Presents the user with hikes near their current location
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search_hikes: {

                Uri searchUri = Uri.parse("geo:" + Double.toString(mLatitude) + "," + Double.toString(mLongitude) + "?q=" + "hikes");
                Log.d("searchUri string: ", searchUri.toString());

                //Create the implicit intent for Google Maps
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, searchUri);

                //If there's an activity associated with this intent, launch it
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                break;
            }

        }
    } //end of onClick function


    //Used to get current weather info
    private void loadWeatherData(String location) {
        new HikesFragment.FetchWeatherTask().execute(location);
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

        Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
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
                    mTvTemp.setText("" + Math.round(mWeatherData.getTemperature().getTemp() - 273.15) + " C");
                    mTvHighTemp.setText("" + Math.round(mWeatherData.getTemperature().getMaxTemp() - 273.15) + " C");
                    mTvLowTemp.setText("" + Math.round(mWeatherData.getTemperature().getMinTemp() - 273.15) + " C");

                    //WILL HAVE TO DO MORE PROCESSING OF RAIN AND SNOW FOR PRECIPITATION POSSIBLY
                    double precipAmount = mWeatherData.getRain().getAmount() + mWeatherData.getSnow().getAmount();
                    mTvPrecip.setText("" +  precipAmount + "in"); //MAY NEED TO CHANGE UNITS
                }
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
//        Log.d("HikesFragment.java: ", "in onLocationChanged() in HikesFragment.java");
//        mLatitude = location.getLatitude();
//        Log.d("mLatitude: ", Double.toString(mLatitude));
//        mLongitude = location.getLongitude();
//        Log.d("mLongitude: ", Double.toString(mLongitude));
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