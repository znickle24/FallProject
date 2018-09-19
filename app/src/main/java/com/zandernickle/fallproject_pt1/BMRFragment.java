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
    private double weight;
    private View mFr_view;
    private boolean mAmerican = false;
    private static final int inch = 12;
    private int inches;
    private int mAge;
    private Bundle mArgsReceived;
    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        savedInstanceState.putDouble("BMRFragment", BMR);
        savedInstanceState.putDouble(Key.WEIGHT, weight);
        savedInstanceState.putInt("INCHES", inches);
        savedInstanceState.putInt(Key.AGE, mAge);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFr_view = inflater.inflate(R.layout.fragment_bmr, container, false);

        if (getArguments() != null) {
            mArgsReceived = getArguments();
        }
        weight = mArgsReceived.getDouble(Key.WEIGHT);
        mAge = mArgsReceived.getInt(Key.AGE);
        if (mArgsReceived.getString(Key.COUNTRY) == "USA") {
            mAmerican = true;
        }
        //formulas differ depending on whether you're a male or female
        if (Sex.MALE != null) {
            BMR = 66 + (6.23 * weight) + (12.7 * inches) - (6.8 * mAge);
        } else { //is female
            BMR = 655 + ( 4.35 * weight ) + ( 4.7 * inches) - ( 4.7 * mAge);
        }
        //for BMRFragment to be accurate, you need to multiply it by your activity level
        //may want to add different levels of activity
        if (ActivityLevel.ACTIVE != null) {
            BMR *= 1.725;
        } else if (ActivityLevel.MODERATELY_ACTIVE != null) {
            BMR *= 1.55;
        }else { //is sedentary
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
