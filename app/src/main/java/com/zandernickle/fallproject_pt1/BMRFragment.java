package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BMRFragment.OnFragmentInteractionListener} interface
 */
public class BMRFragment extends Fragment {
    private double BMR;
    private int mWeight;
    private View mFr_view;
    private boolean mAmerican = false;
    private static final int mFeet = 12;
    private int mInches;
    private String mHeight;
    private int mAge;
    private Bundle mArgsReceived;
    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        savedInstanceState.putDouble("BMRFragment", BMR);
        savedInstanceState.putInt(Key.WEIGHT, mWeight);
        savedInstanceState.putInt("INCHES", mInches);
        savedInstanceState.putInt(Key.AGE, mAge);
        savedInstanceState.putString(Key.HEIGHT, mHeight);
        super.onSaveInstanceState(savedInstanceState);
    }

    private OnFragmentInteractionListener mListener;

    public BMRFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public String getHeightAmerican(int numberOfInches) {
        String height = "";
        int feet;
        if (numberOfInches % 12 == 0) {
            feet = numberOfInches/mFeet;
            height = String.valueOf(feet) + "\"";
        } else {
            int inch = numberOfInches % 12;
            feet = numberOfInches/mFeet;
            height = String.valueOf(feet) + "\'" + String.valueOf(inch) + "\"";
        }
        return height;
    }
    public String getHeightNonAmerican(int numberOfCm) {
        String height = "";
        int cm;
        if (numberOfCm < 100) {

            height = String.valueOf(numberOfCm) + "cm.";
        } else {
            double m = numberOfCm/100;
            height = String.valueOf(m) + "m.";
        }
        return height;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFr_view = inflater.inflate(R.layout.fragment_bmr, container, false);

        if (getArguments() != null) {
            mArgsReceived = getArguments();
        }
        mWeight = mArgsReceived.getInt(Key.WEIGHT);
        mAge = mArgsReceived.getInt(Key.AGE);
        mInches = mArgsReceived.getInt(Key.HEIGHT);

        if (mArgsReceived.getString(Key.COUNTRY) == "US") {
            mAmerican = true;
        }
        if (mAmerican) {
            mHeight = getHeightAmerican(mInches);
        } else {//is any other nationality
            mHeight = getHeightNonAmerican(mInches);
        }
        //formulas differ depending on whether you're a male or female
        if (Sex.MALE != null) {
            BMR = 66 + (6.23 * mWeight) + (12.7 * mInches) - (6.8 * mAge);
        } else { //is female
            BMR = 655 + ( 4.35 * mWeight) + ( 4.7 * mInches) - ( 4.7 * mAge);
        }
        //for BMRFragment to be accurate, you need to multiply it by your activity level
        //may want to add different levels of activity
        if (ActivityLevel.ACTIVE != null) {
            BMR *= 1.725;
        } else if (ActivityLevel.MODERATELY_ACTIVE != null) {
            BMR *= 1.55;
        }else { //is SEDENTARY
            BMR *= 1.2;
        }
        return mFr_view;
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
}
