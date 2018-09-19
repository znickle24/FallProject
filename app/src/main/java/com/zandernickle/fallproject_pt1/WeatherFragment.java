package com.zandernickle.fallproject_pt1;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import java.net.URL;


public class WeatherFragment extends Fragment, Service implements View.OnClickListener, LocationListener {

    //Variables for TextViews holding location and weather data
    private TextView mTvLocation, mTvTemp, mTvHighTemp, mTvLowTemp,
            mTvPrecip, mTvPressure, mTvHumid;

    //Variables for other UI elements
    private Spinner mSpinner;
    private ImageView mIvProfilePic;
    private Button mBtLocation;

    private WeatherData mWeatherData;
    private Context mContext;

    //Uniquely identify loader
    private static final int SEARCH_LOADER = 11;

    //Uniquely identify string you passed in
    public static final String URL_STRING = "query";


//    public WeatherFragment() {
//        // Required empty public constructor
//
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the HikesFragment layout view
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

//        //ONLY NEED THIS IF WE WANT TO CHANGE LOCATION
//        mBtLocation = (Button) view.findViewById(R.id.button_change_location);
//        mBtLocation.setOnClickListener(this);

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




        double latitude = getLatitude();
        double longitude = getLongitude();
        String currentCityCountry = getCityAndCountry(latitude, longitude);
        loadWeatherData(currentCityCountry);


//        //Get the temperature and humidity data
//        int tempInt = getArguments().getInt("temp");
//        String tempString = Integer.toString(tempInt);
//        int humidInt = getArguments().getInt("humidity");
//        String humidString = Integer.toString(humidInt);
//
//        //Set the full name, age, and occupation strings in TextViews
//        if (tempString != null) {
//            mTvTemp.setText(tempString);
//        }
//        if (humidString != null) {
//            mTvHumid.setText(humidString);
//        }

        //Set the ImageView with the profile pic
        Bitmap thumbnailImage = ReusableUtil.bitmapFromBundle();
        if(thumbnailImage != null){
            mIvProfilePic.setImageBitmap(thumbnailImage);
        }

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    private void loadWeatherData(String location){
        new FetchWeatherTask().execute(location);
    }


    private String getCityAndCountry(double latitude, double longitude) {
        String cityCountryString = "";
        cityCountryString.replace(' ','&');
        return cityCountryString;
    }


    private class FetchWeatherTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... inputStringArray) {
            String location = inputStringArray[0];
            URL weatherDataURL = Network.buildURLFromString(location);
            String jsonWeatherData = null;
            try{
                jsonWeatherData = Network.getDataFromURL(weatherDataURL);
                return jsonWeatherData;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonWeatherData) {
            if (jsonWeatherData!=null){
                try {
                    mWeatherData = JSONWeather.getWeatherData(jsonWeatherData);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                if(mWeatherData != null) {
                    mTvTemp.setText("" + Math.round(mWeatherData.getTemperature().getTemp() - 273.15) + " C");
                    mTvHumid.setText("" + mWeatherData.getCurrentCondition().getHumidity() + "%");
                    mTvPressure.setText("" + mWeatherData.getCurrentCondition().getPressure() + " hPa");
                }
            }
        }
    }


        //CHANGE THIS IF WE WANT TO CHANGE THE LOCATION WITH BUTTON
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.button_change_location:{
                    //Get the string from the edit text and sanitize the input
                    //String inputFromEt = mEtLocation.getText().toString().replace(' ','&');
                    //loadWeatherData(inputFromEt);
                }
                break;
            }
        }




        // Flag for GPS status
        boolean isGPSEnabled = false;

        // Flag for network status
        boolean isNetworkEnabled = false;

        // Flag for GPS status
        boolean canGetLocation = false;

        Location location; // Location
        double latitude; // Latitude
        double longitude; // Longitude

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        // Declaring a Location Manager
        protected LocationManager locationManager;

        public WeatherFragment() {
            //this.mContext = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

                if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
                }

                // Getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // Getting network status
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // No network provider is enabled
                } else {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // If GPS enabled, get latitude/longitude using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return location;
        }


        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app.
         * */
        public void stopUsingGPS(){
            if(locationManager != null){
                locationManager.removeUpdates(com.zandernickle.fallproject_pt1.WeatherFragment.this);
            }
        }


        /**
         * Function to get latitude
         * */
        public double getLatitude(){
            if(location != null){
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }


        /**
         * Function to get longitude
         * */
        public double getLongitude(){
            if(location != null){
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

        /**
         * Function to check GPS/Wi-Fi enabled
         * @return boolean
         * */
        public boolean canGetLocation() {
            return this.canGetLocation;
        }


        /**
         * Function to show settings alert dialog.
         * On pressing the Settings button it will launch Settings Options.
         * */
        public void showSettingsAlert(){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing the Settings button.
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            // On pressing the cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }


        @Override
        public void onLocationChanged(Location location) {
        }


        @Override
        public void onProviderDisabled(String provider) {
        }


        @Override
        public void onProviderEnabled(String provider) {
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }


        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }
    }
