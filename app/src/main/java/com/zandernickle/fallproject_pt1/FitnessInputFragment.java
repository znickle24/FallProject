package com.zandernickle.fallproject_pt1;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.neovisionaries.i18n.CountryCode;

import static com.zandernickle.fallproject_pt1.ReusableUtil.loadDialogFragment;
import static com.zandernickle.fallproject_pt1.ReusableUtil.setOnClickListeners;
import static com.zandernickle.fallproject_pt1.ReusableUtil.setOnSeekBarChangeListeners;
import static com.zandernickle.fallproject_pt1.UnitConversionUtil.insToCms;
import static com.zandernickle.fallproject_pt1.UnitConversionUtil.lbsToKgs;

/**
 * A simple {@link Fragment} subclass.
 */
public class FitnessInputFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, WarningDialogFragment.OnWarningAcceptListener {

    // TODO: Put these in Key.java FOR USE IN ONDATAPASS
    private static final String SIGN_IN_FRAGMENT = "SIGN_IN_FRAGMENT";
    private static final String FITNESS_INPUT_FRAGMENT = "FITNESS_INPUT_FRAGMENT";

    private static final String WELCOME = "WELCOME";

    private static final int MIN_HEIGHT_INCHES = 0;
    private static final int MAX_HEIGHT_INCHES = 96;
    private static final int MIN_WEIGHT_LBS = 0;
    private static final int MAX_WEIGHT_LBS = 500;
    private static final int MAX_DELTA_LBS = 5;
    private static final int SB_DEFAULT_PROG_HEIGHT = (MAX_HEIGHT_INCHES - MIN_HEIGHT_INCHES) / 2;
    private static final int SB_DEFAULT_PROG_WEIGHT = (MAX_WEIGHT_LBS - MIN_WEIGHT_LBS) / 2;
    private static final int SB_DEFAULT_PROG_DELTA = 0;
    private static final int UNHEALTHY_DELTA_LBS = 2;

    private static final String UNITS_HEIGHT_INCHES = "in";
    private static final String UNITS_WEIGHT_LBS = "lbs";
    private static final String UNITS_HEIGHT_CM = "cm";
    private static final String UNITS_WEIGHT_KG = "kg";
    private static final String NEG = "-";
    private static final String POS = "+";

    // Set default units
    private int mMinHeight = MIN_HEIGHT_INCHES;
    private int mMaxHeight = MAX_HEIGHT_INCHES;
    private int mMinWeight = MIN_WEIGHT_LBS;
    private int mMaxWeight = MAX_WEIGHT_LBS;
    private int mMaxDelta = MAX_DELTA_LBS;
    private int mInitProgHeight = SB_DEFAULT_PROG_HEIGHT;
    private int mInitProgWeight = SB_DEFAULT_PROG_WEIGHT;
    private int mInitProgWeightDelta = SB_DEFAULT_PROG_DELTA;
    private int mUnhealthyDelta = UNHEALTHY_DELTA_LBS;

    private String mUnitsHeight = UNITS_HEIGHT_INCHES;
    private String mUnitsWeight = UNITS_WEIGHT_LBS;

    private TextView mTvWelcome, mTvHeightResult, mTvWeightResult, mTvWeightDeltaNeg, mTvWeightDeltaPos, mTvWeightDeltaResult;
    private CustomSeekBar mSbHeight, mSbWeight, mSbWeightDelta;
    private RadioGroup mRgSex, mRgActivityLevel;
    private Button mBtnSubmit;

    private Boolean mHasCompleteData = true;
    private CountryCode mCountryCode;

    private OnDataPass mDataPasser;
    private Bundle mFitnessInputBundle;

    public FitnessInputFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mDataPasser = (FitnessInputFragment.OnDataPass) context;
        } catch (ClassCastException e) {
            String message = context.toString() + " must implement " +
                    FitnessInputFragment.class.toString() + ".OnDataPass";
            throw new ClassCastException(message);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_fitness_input, container, false);

        mTvWelcome = fragment.findViewById(R.id.tv_welcome);

        mTvHeightResult = fragment.findViewById(R.id.tv_height_result);
        mTvWeightResult = fragment.findViewById(R.id.tv_weight_result);
        mTvWeightDeltaNeg = fragment.findViewById(R.id.tv_weight_change_label_neg);
        mTvWeightDeltaPos = fragment.findViewById(R.id.tv_weight_change_label_pos);
        mTvWeightDeltaResult = fragment.findViewById(R.id.tv_weight_delta_result);

        mSbHeight = fragment.findViewById(R.id.sb_height);
        mSbWeight = fragment.findViewById(R.id.sb_weight);
        mSbWeightDelta = fragment.findViewById(R.id.sb_weight_delta);

        mRgSex = fragment.findViewById(R.id.rg_sex);
        mRgActivityLevel = fragment.findViewById(R.id.rg_activity_level);

        mBtnSubmit = fragment.findViewById(R.id.button_submit);

        setOnSeekBarChangeListeners(FitnessInputFragment.this, mSbHeight, mSbWeight, mSbWeightDelta);
        setOnClickListeners(FitnessInputFragment.this, mBtnSubmit);

        return fragment;
    }

    // call this first
    private void convertUnitsToLocale(CountryCode countryCode) {
        if (countryCode != CountryCode.US) {
            mMinHeight = insToCms(mMinHeight);
            mMaxHeight = insToCms(mMaxHeight);
            mMinWeight = lbsToKgs(mMinWeight);
            mMaxWeight = lbsToKgs(mMaxWeight);
            mMaxDelta = lbsToKgs(mMaxDelta);
            mInitProgHeight = insToCms(mInitProgHeight);
            mInitProgWeight = lbsToKgs(mInitProgWeight);
            mInitProgWeightDelta = lbsToKgs(mInitProgWeightDelta);
            mUnitsHeight = UNITS_HEIGHT_CM;
            mUnitsWeight = UNITS_WEIGHT_KG;
            mUnhealthyDelta = lbsToKgs(mUnhealthyDelta);
        }
    }

    // should call this second
    private void initializeSeekBars() {
        mSbHeight.setBounds(mMinHeight, mMaxHeight);
        mSbWeight.setBounds(mMinWeight, mMaxWeight);
        mSbWeightDelta.setBounds(-mMaxDelta, mMaxDelta);

        mSbHeight.setProgress(mInitProgHeight);
        mSbWeight.setProgress(mInitProgWeight);
        mSbWeightDelta.setProgress(mInitProgWeightDelta);
    }

    // Should call this third
    private void initializeSeekBarLabelValues() {
        mTvHeightResult.setText(mSbHeight.getProgress() + " " + mUnitsHeight);
        mTvWeightResult.setText(mSbWeight.getProgress() + " " + mUnitsWeight);
        mTvWeightDeltaResult.setText(mSbWeightDelta.getProgress() + " " + mUnitsWeight);
        mTvWeightDeltaNeg.setText(NEG + mMaxDelta + " " + mUnitsWeight);
        mTvWeightDeltaPos.setText(POS + mMaxDelta + " " + mUnitsWeight);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle signInBundle = getArguments();
        String uppercaseName = signInBundle.getString(Key.NAME).toUpperCase();
        mCountryCode = (CountryCode) signInBundle.getSerializable(Key.COUNTRY);

        mTvWelcome.setText(WELCOME + " " + uppercaseName);

        convertUnitsToLocale(mCountryCode);
        initializeSeekBars();
        initializeSeekBarLabelValues();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_submit:

                Sex sex = getRadioSelectedSex(mRgSex.getCheckedRadioButtonId());
                ActivityLevel activityLevel = getRadioSelectedActivityLevel(mRgActivityLevel.getCheckedRadioButtonId());

                if (sex == null || activityLevel == null) {
                    mHasCompleteData = false;
                    // TODO: Show dialog
                    break;
                }

                int height = mSbHeight.getProgress();
                int weight = mSbWeight.getProgress();

                int weightDelta = mSbWeightDelta.getProgress();
                if (Math.abs(weightDelta) > mUnhealthyDelta) {
                    // Show dialog but do not set complete to false -- the user may choose to proceed.
                    loadDialogFragment(getFragmentManager(), new WarningDialogFragment(),
                            FitnessInputFragment.this, "test", false);
                }

                Goal fitnessGoal = getFitnessGoalEnum(weightDelta);

                // Nothing will be null at this point
                mFitnessInputBundle = new Bundle();
                mFitnessInputBundle.putSerializable(Key.SEX, sex);
                mFitnessInputBundle.putSerializable(Key.ACTIVITY_LEVEL, activityLevel);
                mFitnessInputBundle.putInt(Key.HEIGHT, height);
                mFitnessInputBundle.putInt(Key.WEIGHT, weight);
                // TODO: Technically, could just pass neg, 0, or pos as Key.WEIGHT_GOAL value.
                // No essential need for Key.GOAL (implicit in value of Key.WEIGHT_GOAL.
                mFitnessInputBundle.putSerializable(Key.GOAL, fitnessGoal);
                mFitnessInputBundle.putSerializable(Key.WEIGHT_GOAL, weightDelta);

                // Data can be passed here or on reception of data from warning dialog.
                // Better way to avoid multiple exit points?
                mDataPasser.onDataPass(FITNESS_INPUT_FRAGMENT, mFitnessInputBundle);
                break;
        }
    }

    @Override
    public void onWarningAccepted() {
        mDataPasser.onDataPass(FITNESS_INPUT_FRAGMENT, mFitnessInputBundle);
        Log.d("Log", "SUCCESS"); // TODO: Interface with BMRFragment
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        switch (seekBar.getId()) {
            case R.id.sb_height:
                mTvHeightResult.setText(seekBar.getProgress() + mUnitsHeight);
                break;
            case R.id.sb_weight:
                mTvWeightResult.setText(seekBar.getProgress() + mUnitsWeight);
                break;
            case R.id.sb_weight_delta:
                mTvWeightDeltaResult.setText(seekBar.getProgress() + " " + mUnitsWeight);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Nullable
    private Sex getRadioSelectedSex(int buttonId) {

        Sex sex = null;
        switch (buttonId) {
            case R.id.rb_male:
                sex = Sex.MALE;
                break;
            case R.id.rb_female:
                sex = Sex.FEMALE;
                break;
            default:
                break;
        }
        return sex;
    }

    @Nullable
    private ActivityLevel getRadioSelectedActivityLevel(int buttonId) {

        ActivityLevel activityLevel = null;
        switch (buttonId) {
            case R.id.rb_sedentary:
                activityLevel = ActivityLevel.SEDENTARY;
                break;
            case R.id.rb_moderately_active:
                activityLevel = ActivityLevel.MODERATELY_ACTIVE;
                break;
            case R.id.rb_active:
                activityLevel = ActivityLevel.ACTIVE;
                break;
            default:
                break;
        }
        return activityLevel;
    }

    // Can be negative too, should not be null
    private Goal getFitnessGoalEnum(int weightDelta) {
        Goal weightGoal = Goal.MAINTAIN_WEIGHT;
        if (weightDelta < 0) {
            weightGoal = Goal.LOSE_WEIGHT;
        } else if (weightDelta > 0) {
            weightGoal = Goal.GAIN_WEIGHT;
        }
        return weightGoal;
    }

    public interface OnDataPass {
        void onDataPass(String key, Bundle signInBundle);
    }
}
