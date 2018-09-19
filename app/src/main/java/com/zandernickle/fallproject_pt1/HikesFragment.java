package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HikesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HikesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HikesFragment extends Fragment implements View.OnClickListener, LocationListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //Create variables for the UI elements
    private TextView mTvTemp, mTvHumid;
    private Button mButtonSearch;
    private ImageView mIvProfilePic;

    //Variables that hold the current location's latitude and longitude
    private double mLatitude, mLongitude;

    Context mContext;

    //Used to find current location
    protected LocationManager locationManager;


    /**
     * Constructor for HikesFragment
     */
    public HikesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HikesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HikesFragment newInstance(String param1, String param2) {
        HikesFragment fragment = new HikesFragment();
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
        View view = inflater.inflate(R.layout.fragment_hikes, container, false);

        //Get hike search button
        mButtonSearch = (Button) view.findViewById(R.id.button_hike_search);

        //Set hike search button to an onClickListener
        mButtonSearch.setOnClickListener(this);

        //Get the TextViews to display the temperature and humidity
        mTvTemp = (TextView) view.findViewById(R.id.tv_temp_data);
        mTvHumid = (TextView) view.findViewById(R.id.tv_humid_data);

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

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context; //added; MIGHT NOT NEED

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




    /**
     * Handles clicks for the hike search button
     * Finds the user's current location
     * Presents the user with hikes near their current location
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_hike_search: {

                //Get current location
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
                    return;
                }
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, listener);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                Location location;

                if(network_enabled){

                    location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if(location!=null){
                        mLatitude = location.getLatitude();
                        Log.d("mLatitude: ", Double.toString(mLatitude));
                        mLongitude = location.getLongitude();
                        Log.d("mLongitude: ", Double.toString(mLongitude));

                    }
                }

                Uri searchUri = Uri.parse("geo:" + Double.toString(mLatitude) + "," + Double.toString(mLongitude) + "?q=" + "hikes");

                //Create the implicit intent for Google Maps
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, searchUri);

                //If there's an activity associated with this intent, launch it
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                    }
                }
        }
    } //end of onClick function



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