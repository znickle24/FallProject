package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.i18n.CountryCode;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class BMRFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private TextView mTv_BMR;
    private TextView mTv_BMR_data;
    private Button mButton_BMI_calculator;
    private TextView mTv_BMI_data;
    private boolean mGainWeight = false;
    private boolean mLoseWeight = false;
    private boolean mMaintainWeight = false;
    private double BMR; //value calculated based on age, sex, height, & weight
    private int mCalorieIntake; //the number of calories an individual needs to eat to meet their goal
    private double mBMI = -1.0; //value calculated based on height, weight (metric or imperial) set to -1 if the user hasn't calculated it before.
    private int mWeight; //value in lbs (imperial) or kgs (metric)
    private int mWeightGoal; //represents the number of lbs a user wants to lose or gain/week
    private View mFr_view; //view to be returned from onCreateView
    private boolean mAmerican = false; //used to determine metric or imperial calculation
    private static final int mFeet = 12; //used to calculate height in Amerians
    private int mInches; //value passed from Activity as Height
    private String mHeight; //height presented in users national form
    private int mAge;
    private Bundle mArgsReceived; //arguments passed from Activity
    //values from bmi calculation
    private static final String mUnderweight = "Underweight";
    private static final String mNormalWeight = "Normal Weight";
    private static final String mOverweight = "Overweight";
    private static final String mObese = "Obese";
    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        savedInstanceState.putDouble("BMRFragment", BMR);
        savedInstanceState.putInt(Key.WEIGHT, mWeight);
        savedInstanceState.putInt("INCHES", mInches);
        savedInstanceState.putInt(Key.AGE, mAge);
        savedInstanceState.putString(Key.HEIGHT, mHeight);
        super.onSaveInstanceState(savedInstanceState);
    }

    public BMRFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Set the correct bool to true for the participants goal
    public void getGoal() {
        if (Goal.GAIN_WEIGHT != null) {
            mGainWeight = true;
        } else if (Goal.LOSE_WEIGHT != null) {
            mLoseWeight = true;
        } else {
            mMaintainWeight = true;
        }
    }
    //Design decision was to make anyone in the US show height in feet/inches instead of cm
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

    //Design decision was to make anyone outside of the US show height in cm instead of feet/inches
    //Using traditional British form of showing height in cm
    public String getHeightNonAmerican(int numberOfCm) {
        String height = String.valueOf(numberOfCm) + "cm.";
        return height;
    }

    /*
    Returns a double based on weight and height. Works for metric and imperial depending on
    home country.
     */
    public double calculateBMI() {
        double bmi;
        if (mAmerican) {
            bmi = (mWeight * 703)/mInches;
        } else {
            double heightInMeters = Double.parseDouble(mHeight)/100;
            bmi = mWeight / Math.pow(heightInMeters, 2);
        }
        return bmi;
    }
    /*
    Returns a string based on the bmi (double) passed in. The string is one of the following:
    * Underweight
    * Normal Weight
    * Overweight
    * Obese
     */
    public String determineBMICategory(double bmi) {
        String ret = "";
        if (bmi < 18.5) {
            ret = mUnderweight;
        } else if (bmi < 24.9) {
            ret = mNormalWeight;
        } else if (bmi < 29.9) {
            ret = mOverweight;
        } else {
            ret = mObese;
        }
        return ret;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFr_view = inflater.inflate(R.layout.fragment_bmr, container, false);

        //grab each of the views that will be filled with the information
        mTv_BMR = mFr_view.findViewById(R.id.tv_bmr);
        mTv_BMR_data = mFr_view.findViewById(R.id.tv_bmr_data);
        mTv_BMI_data = mFr_view.findViewById(R.id.tv_calculate_bmi_data);
        mButton_BMI_calculator = mFr_view.findViewById(R.id.button_calculate_bmi);

        //null check to make sure that the previous activity/fragment sent over the information
        if (getArguments() != null) {
            mArgsReceived = getArguments();
        }
        //set the variables needed in calculating BMR and BMI
        mWeight = mArgsReceived.getInt(Key.WEIGHT);
        mAge = mArgsReceived.getInt(Key.AGE);
        mInches = mArgsReceived.getInt(Key.HEIGHT);
        mWeightGoal = mArgsReceived.getInt(Key.GOAL);

        getGoal();;

        if (mArgsReceived.get(Key.COUNTRY) == CountryCode.US) {
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

        /*
        * Calorie intake is based on their goal. Depending on if they're American or not, calc will be different.
        * Per lb lost/gained, suggested calorie discount/increase per week is 500
        * Per kg lost/gained, suggested kCal discount/increase per week is 1,100
         */
        if (mWeightGoal < 0) {
            mLoseWeight = true;
        } else if (mWeightGoal == 0) {
            mMaintainWeight = true;
        } else {
            mGainWeight = true;
        }
        mCalorieIntake = (int) BMR;
        if (mAmerican) {
            //values are either positive, negative or 0. They represent lbs for Americans
            mWeightGoal *= 500;
            mCalorieIntake += mWeightGoal;
            if (mLoseWeight) {
                if (Sex.MALE != null && mCalorieIntake < 1200) {
                    Toast.makeText(getActivity(), "You are running a health risk by consuming below 1200 Calories a day",
                            Toast.LENGTH_SHORT).show();
                } else if (Sex.FEMALE != null && mCalorieIntake < 1000) {
                    Toast.makeText(getActivity(), "You are running a health risk by consuming below 1000 Calories a day",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else { //not American
            //values are either positive, negative, or 0. They represents kg for non-Americans
            mWeightGoal *= 1100;
            mCalorieIntake += mWeightGoal;
            if (mLoseWeight) {
                if (Sex.MALE != null && mCalorieIntake < 1200) {
                    Toast.makeText(getActivity(), "You are running a health risk by consuming below 1200 Calories a day",
                            Toast.LENGTH_SHORT).show();
                } else if (Sex.FEMALE != null && mCalorieIntake < 1000) {
                    Toast.makeText(getActivity(), "You are running a health risk by consuming below 1000 Calories a day",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        mTv_BMR.setText("BMR: ");
        mTv_BMR_data.setText("You need " + mCalorieIntake + "to meet your weight goal.");

        /*
        Values for BMR, Calorie Intake, Activity Level, Weight, Height, Nationality have all been calculated
        Return the view
         */

        return mFr_view;
    }

    /*
    * The BMI is only calculated when the user presses the Calculate BMI button. This method calculates
    * the users BMI based on the information provided in the onboarding process. Once we have this
    * saved for the user, we will check to see if they've calculated their BMI in the past. If
    * there is an improvement in their BMI (meaning it has lowered), we will congratulate them with a
    * toast.
    *
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_calculate_bmi:
                if (mBMI != -1) {
                    //They've calculated this before. Check to see if they've improved their BMI.
                    double oldBMI = mBMI;
                    mBMI = calculateBMI();
                    if (oldBMI > mBMI) {
                        Toast.makeText(getActivity(), "Congratulations on improving your BMI!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mBMI = calculateBMI();
                }
                String weightStatus = determineBMICategory(mBMI);
                mTv_BMI_data.setText(String.valueOf("Your BMI is: " + mBMI));
                switch (weightStatus) {
                    case mUnderweight:
                        Toast.makeText(getActivity(), "You appear to be underweight, may want to consume more calories",
                                Toast.LENGTH_SHORT).show();
                    case mNormalWeight:
                        Toast.makeText(getActivity(), "Great! You are in the normal range for BMI",
                                Toast.LENGTH_SHORT).show();
                    case mOverweight:
                        Toast.makeText(getActivity(), "You appear to be overweight, may want to cut some calories",
                                Toast.LENGTH_SHORT).show();
                    case mObese:
                        Toast.makeText(getActivity(), "You fall in the obese rage for this metric." +
                                        "You may consider visiting a physician due to health risks.",
                                Toast.LENGTH_SHORT).show();
                }
            default:
                break;
        }
    }
}
