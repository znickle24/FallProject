package com.zandernickle.fallproject_pt1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
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

import static com.zandernickle.fallproject_pt1.ReusableUtil.loadMenuBarFragment;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class BMRFragment extends android.support.v4.app.Fragment implements View.OnClickListener {



    private TextView mTv_BMR, mTv_weight_goal_to_label, mTv_weight_goal_data, mTv_BMI;
    private Button mBtn_update_goals;


    private boolean mLoseWeight = false;
    private static double BMR = -1.0; //value calculated based on age, sex, height, & weight
    private int mCalorieIntake; //the number of calories an individual needs to eat to meet their goal
    private static double mBMI = -1.0; //value calculated based on height, weight (metric or imperial) set to -1 if the user hasn't calculated it before.
    private static int mWeight = -1; //value in lbs (imperial) or kgs (metric)
    private int mWeightGoal = -1; //represents the number of lbs a user wants to lose or gain/week
    private View mFr_view; //view to be returned from onCreateView
    private static boolean mAmerican = false; //used to determine metric or imperial calculation
    private static final int mFeet = 12; //used to calculate height in Amerians
    private static int mInches = -1; //value passed from Activity as Height
    private static String mHeight; //height presented in users national form
    private static int mAge = -1;
    private Bundle mArgsReceived; //arguments passed from Activity
    private User mUser;
    private int mUserId;
    private UserRepository mUserRepo;
    private AndroidViewModel mViewModel;
    //values from bmi calculation
    private static final String mUnderweight = "Underweight";
    private static final String mNormalWeight = "Normal Weight";
    private static final String mOverweight = "Overweight";
    private static final String mObese = "Obese";
    private OnDataPass mDataPasser;

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (BMR != -1) {
                savedInstanceState.putDouble(Key.BMR, BMR);
            }
            if (mBMI != -1) {
                savedInstanceState.putDouble(Key.BMI, mBMI);
            }
            if (mWeight != -1) {
                savedInstanceState.putInt(Key.WEIGHT, mWeight);
            }
            if (mWeightGoal != -1) {
                savedInstanceState.putInt(Key.GOAL, mWeightGoal);
            }
            if (mInches != -1) {
                savedInstanceState.putInt("INCHES", mInches);
            }
            if (mAge != -1) {
                savedInstanceState.putInt(Key.AGE, mAge);
            }
            if (mHeight != null) {
                savedInstanceState.putString(Key.HEIGHT, mHeight);
            }
            if (mCalorieIntake != -1) {
                savedInstanceState.putInt("CalorieIntake", mCalorieIntake);
            }
            savedInstanceState.putBoolean("Lose_Weight", mLoseWeight);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (BMR != -1) {
//                BMR = savedInstanceState.getDouble(Key.BMR);
//                mTv_BMR.setText("You need " + BMR + " Calories in order to meet your weight goal");
                mTv_BMR = mFr_view.findViewById(R.id.tv_bmr);
                mTv_BMR.setText("Your BMR (Basal Metabolic Rate) is " + mCalorieIntake + " calories / day");
            }
            if (mBMI != -1) {
                mBMI = savedInstanceState.getDouble(Key.BMI);
//                mTv_BMI_data = mFr_view.findViewById(R.id.tv_calculate_bmi_data);
//                mTv_BMI_data.setText(String.valueOf("Your BMI is: " + mBMI));

                mTv_BMI = mFr_view.findViewById(R.id.tv_bmi);
                mTv_BMI.setText(String.valueOf("Your BMI (Body Mass Index) is " + mBMI)); // TODO: Instead of toast, concatenate underweight/overweight/normal to string.

            }
            if (mWeight != -1) {
                mWeight = savedInstanceState.getInt(Key.WEIGHT);
            }
            if (mWeightGoal != -1) {
                savedInstanceState.getInt(Key.GOAL);
            }
            if (mInches != -1) {
                mInches = savedInstanceState.getInt("INCHES");
            }
            if (mAge != -1) {
                mAge = savedInstanceState.getInt(Key.AGE);
            }
            if (mHeight != null) {
                mHeight = savedInstanceState.getString(Key.HEIGHT);
            }
            if (mCalorieIntake != -1) {
                mCalorieIntake = savedInstanceState.getInt("CalorieIntake");
                if (mLoseWeight) {
                    mTv_weight_goal_to_label.setText("To lose " + Math.abs(mWeightGoal) + " 2 lbs per week, you must eat"); // TODO: Determine weight goal here (add, maintain, or lose);
                } else {
                    mTv_weight_goal_to_label.setText("To gain " + mWeightGoal + " lbs per week, you must eat");
                }
                mTv_weight_goal_data.setText(mCalorieIntake + " CAL"); // TODO: Confirm (was losing this data on orientation change).
            }
            mLoseWeight = savedInstanceState.getBoolean("Lose_Weight");
        }
        super.onViewStateRestored(savedInstanceState);
    }

    public BMRFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // TODO: Shouldn't need to worry about all the lifecycle stuff
    }

    //Design decision was to make anyone in the US show height in feet/inches instead of cm
    public static String getHeightAmerican(int numberOfInches) {
        String height = "";
        int feet;
        if (numberOfInches % 12 == 0) {
            feet = numberOfInches/mFeet;
            height = String.valueOf(feet) + "\'";
        } else {
            int inch = numberOfInches % 12;
            feet = numberOfInches/mFeet;
            height = String.valueOf(feet) + "\'" + String.valueOf(inch) + "\"";
        }
        return height;
    }

    //Design decision was to make anyone outside of the US show height in cm instead of feet/inches
    //Using traditional British form of showing height in cm
    public static String getHeightNonAmerican(int numberOfCm) {
        String height = String.valueOf(numberOfCm) + "cm.";
        return height;
    }

    /*
    Returns a double based on weight and height. Works for metric and imperial depending on
    home country.
     */
    public static double calculateBMI() {
        double bmi;
        if (mAmerican) {
            bmi = ((mWeight)/Math.pow(mInches, 2)) * 703;
        } else {
            //need to take the cm out of mHeight then should work
            double numberOfMeters = 0.0;
            double additionalCM = 0.0;
            if (mInches > 100) {
                if (mInches > 200) {
                    //unlikely, but plenty of people surpass this
                    numberOfMeters = 2;
                    additionalCM = mInches - 200;
                } else {
                    //regular case
                    numberOfMeters = 1;
                    additionalCM = mInches - 100;
                }
            } else {
                numberOfMeters = 0;
            }
            additionalCM = additionalCM/100.0;
            double heightInMeters = numberOfMeters + additionalCM;
            bmi = mWeight / Math.pow(heightInMeters, 2);
        }
        return bmi;
    }

    /*
    * Returns a double representing the number of calories a day you need in order to maintain your
    * current weight. Made static for testing.
     */
    public static double calculateBMR() {
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
        return BMR;
    }
    /*
    Returns a string based on the bmi (double) passed in. The string is one of the following:
    * Underweight
    * Normal Weight
    * Overweight
    * Obese
     */
    public static String determineBMICategory(double bmi) {
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
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mDataPasser = (BMRFragment.OnDataPass) context;
        } catch (ClassCastException e) {
            String message = context.toString() + " must implement " +
                    FitnessInputFragment.class.toString() + ".OnDataPass";
            throw new ClassCastException(message);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mFr_view = inflater.inflate(R.layout.fragment_bmr, container, false);

        //grab each of the views that will be filled with the information
        mTv_BMR = mFr_view.findViewById(R.id.tv_bmr);
        mTv_BMR = mFr_view.findViewById(R.id.tv_bmr);
        mTv_BMI = mFr_view.findViewById(R.id.tv_bmi);
        mTv_weight_goal_to_label = mFr_view.findViewById(R.id.tv_weight_goal_top_label);
        mTv_weight_goal_data = mFr_view.findViewById(R.id.tv_weight_goal_data);
        mBtn_update_goals = mFr_view.findViewById(R.id.button_update_goals);
        mBtn_update_goals.setOnClickListener(this);


        //null check to make sure that the previous activity/fragment sent over the information
        if (getArguments() != null) {
            mArgsReceived = getArguments();
        }
        if (savedInstanceState == null) {
            //grab the user
            mViewModel = ViewModelProviders.of(this.getActivity()).get(BMRViewModel.class);
            mUserRepo = new UserRepository(this.getActivity().getApplication());
            //set the variables needed in calculating BMR and BMI
            mWeight = mArgsReceived.getInt(Key.WEIGHT);
            mAge = mArgsReceived.getInt(Key.AGE);
            mInches = mArgsReceived.getInt(Key.HEIGHT);
            BMR = mArgsReceived.getInt(Key.BMR);
            mUser = mArgsReceived.getParcelable(Key.USER);
            mWeightGoal = mUser.getWeightGoal();

            if (mArgsReceived.get(Key.COUNTRY) == CountryCode.US) {
                mAmerican = true;
            }

            if (mAmerican) {
                mHeight = getHeightAmerican(mInches);
            } else {//is any other nationality
                mHeight = getHeightNonAmerican(mInches);
            }

            BMR = calculateBMR();
            /*
            * Calorie intake is based on their goal. Depending on if they're American or not, calc will be different.
            * Per lb lost/gained, suggested calorie discount/increase per week is 500
            * Per kg lost/gained, suggested kCal discount/increase per week is 1,100
             */
            if (mWeightGoal < 0) {
                mLoseWeight = true;
            }

            mCalorieIntake = (int) BMR;
            int weightGoal = 0;
            if (mAmerican) {
                //values are either positive, negative or 0. They represent lbs for Americans
                weightGoal = mWeightGoal * 500;
                mCalorieIntake += weightGoal;
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
                weightGoal =  mWeightGoal * 1100;
                mCalorieIntake += weightGoal;
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

            mTv_BMR.setText("Your BMR (Basal Metabolic Rate) is " + mCalorieIntake + " calories / day");
            if (mLoseWeight) {
                mTv_weight_goal_to_label.setText("To lose " + Math.abs(mWeightGoal) + " lbs per week, you must eat");
            } else {
                mTv_weight_goal_to_label.setText("To gain"  + mWeightGoal + " lbs per week, you must eat");
            }
            mTv_weight_goal_data.setText(mCalorieIntake + " CAL");
            mBMI = calculateBMI();
            mTv_BMI.setText(String.valueOf(mBMI));
            mUser.setBMR((int) BMR);
            mUser.setBMI((int) mBMI);
            mUser.setCalorieIntake(mCalorieIntake);
            mUserRepo.updateUserAsync(mUser);
        }

        // Don't change this -- the menu bar with functioning navigation
        loadMenuBarFragment(BMRFragment.this, mArgsReceived, R.id.fl_menu_bar_fragment_placeholder);

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
            case R.id.button_update_goals: { // TODO: Changed this ... should just need to send back .onDataPass(Module.UpdateGoals, null) which will then load a new fitnesInput fragment from Main and redirect to this fragment when finished
                //check to see if they've already calculated the BMI in the past
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
                mTv_BMI.setText(String.valueOf("Your BMI (Body Mass Index) is " + mBMI)); // TODO: Instead of toast, concatenate underweight/overweight/normal to string.
//                switch (weightStatus) {
//                    case mUnderweight:
//                        Toast.makeText(getActivity(), "You appear to be underweight, may want to consume more calories",
//                                Toast.LENGTH_SHORT).show();
//                    case mNormalWeight:
//                        Toast.makeText(getActivity(), "Great! You are in the normal range for BMI",
//                                Toast.LENGTH_SHORT).show();
//                    case mOverweight:
//                        Toast.makeText(getActivity(), "You appear to be overweight, may want to cut some calories",
//                                Toast.LENGTH_SHORT).show();
//                    case mObese:
//                        Toast.makeText(getActivity(), "You fall in the obese rage for this metric." +
//                                        "You may consider visiting a physician due to health risks.",
//                                Toast.LENGTH_SHORT).show();
//                }
                mDataPasser.onDataPass(Module.UPDATE_GOALS, null);
            }
//
//
//            }
            default:
                break;
        }
    }
    /**
     * An interface to communicate with this Fragment's host Activity.
     */
    public interface OnDataPass {
        void onDataPass(Module moduleToLoad, Bundle fitnessBundle);
    }
}
