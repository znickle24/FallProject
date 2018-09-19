package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView mTvTemp, mTvPressure, mTvHumid;
    private WeatherData mWeatherData;
    private Button mBtLocation;
    private ImageView mIvProfilePic;

    private Context mContext;

    //Uniquely identify loader
    private static final int SEARCH_LOADER = 11;

    //Uniquely identify string you passed in
    public static final String URL_STRING = "query";


    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the HikesFragment layout view
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        //ONLY NEED THIS IF WE WANT TO CHANGE LOCATION
        mBtLocation = (Button) view.findViewById(R.id.button_change_location);
        mBtLocation.setOnClickListener(this);

        //Get the TextViews to display the temperature and humidity
        mTvTemp = (TextView) view.findViewById(R.id.tv_temp_data);
        mTvHumid = (TextView) view.findViewById(R.id.tv_humid_data);
        mTvPressure = (TextView) view.findViewById(R.id.tv_pressure_data);

        //Get the temperature and humidity data
        int tempInt = getArguments().getInt("temp");
        String tempString = Integer.toString(tempInt);
        int humidInt = getArguments().getInt("humidity");
        String humidString = Integer.toString(humidInt);

        //Set the full name, age, and occupation strings in TextViews
        if (tempString != null) {
            mTvTemp.setText(tempString);
        }
        if (humidString != null) {
            mTvHumid.setText(humidString);
        }

        //Set the image view
        String imagePath = getArguments().getString("imagePath");
        Bitmap thumbnailImage = BitmapFactory.decodeFile(imagePath);
        if(thumbnailImage != null){
            mIvProfilePic.setImageBitmap(thumbnailImage);
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void loadWeatherData(String location){
        new FetchWeatherTask().execute(location);
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
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
                if(mWeatherData!=null) {
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


}
