package com.zandernickle.fallproject_pt1;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.neovisionaries.i18n.CountryCode;

import static com.zandernickle.fallproject_pt1.Key.FITNESS_INPUT_FRAGMENT;
import static com.zandernickle.fallproject_pt1.ReusableUtil.setOnClickListeners;
import static com.zandernickle.fallproject_pt1.ReusableUtil.setOnSeekBarChangeListeners;
import static com.zandernickle.fallproject_pt1.UnitConversionUtil.insToCms;
import static com.zandernickle.fallproject_pt1.UnitConversionUtil.lbsToKgs;

/**
 * A simple {@link Fragment} subclass.
 */
public class FitnessInputFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, WarningDialogFragment.OnWarningAcceptListener {

    private static final String UNHEALTHY_GOAL_DIALOG = "UNHEALTHY_GOAL_DIALOG";

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

    private CountryCode mCountryCode;
    private Bundle mFitnessInputBundle; // required to communicate with dialog

    private OnDataPass mDataPasser;

    public FitnessInputFragment() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     *
     * Attaches this Fragment's OnDataPass interface to the host Activity (Main).
     */
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

    /**
     * {@inheritDoc}
     *
     * Does the usual inflation stuff and sets up any action listeners.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_fitness_input, container, false);

        mTvWelcome = thisFragment.findViewById(R.id.tv_welcome);
        mTvHeightResult = thisFragment.findViewById(R.id.tv_height_result);
        mTvWeightResult = thisFragment.findViewById(R.id.tv_weight_result);
        mTvWeightDeltaNeg = thisFragment.findViewById(R.id.tv_weight_change_label_neg);
        mTvWeightDeltaPos = thisFragment.findViewById(R.id.tv_weight_change_label_pos);
        mTvWeightDeltaResult = thisFragment.findViewById(R.id.tv_weight_delta_result);
        mSbHeight = thisFragment.findViewById(R.id.sb_height);
        mSbWeight = thisFragment.findViewById(R.id.sb_weight);
        mSbWeightDelta = thisFragment.findViewById(R.id.sb_weight_delta);
        mRgSex = thisFragment.findViewById(R.id.rg_sex);
        mRgActivityLevel = thisFragment.findViewById(R.id.rg_activity_level);
        mBtnSubmit = thisFragment.findViewById(R.id.button_submit);

        setOnSeekBarChangeListeners(FitnessInputFragment.this, mSbHeight, mSbWeight, mSbWeightDelta);
        setOnClickListeners(FitnessInputFragment.this, mBtnSubmit);

        return thisFragment;
    }

    /**
     * {@inheritDoc}
     *
     *
     *
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle signInBundle = getArguments();
        String uppercaseName = signInBundle.getString(Key.NAME).toUpperCase();
        mCountryCode = (CountryCode) signInBundle.getSerializable(Key.COUNTRY);

        mTvWelcome.setText(WELCOME + " " + uppercaseName);

        initializeUnitsToLocale(mCountryCode);
        initializeSeekBars();
        initializeSeekBarLabelValues();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_submit:

                int height = mSbHeight.getProgress();
                int weight = mSbWeight.getProgress();

                Sex sex = getRadioSelectedSex(mRgSex.getCheckedRadioButtonId());
                ActivityLevel activityLevel = getRadioSelectedActivityLevel(
                        mRgActivityLevel.getCheckedRadioButtonId());

                int delta = mSbWeightDelta.getProgress();
                if (isUnhealthyGoal(delta)) {
                    /* The user's fitness data should not be updated if they select the dialog's back
                     * button (as would happen if this condition did not break the switch statement.
                     * Instead, the user should be able to update their weight goal and click the
                     * submit button again.
                     */
                    displayDialog(UNHEALTHY_GOAL_DIALOG);
                    break;
                }

                mFitnessInputBundle = new Bundle();

                mFitnessInputBundle.putSerializable(Key.SEX, sex);
                mFitnessInputBundle.putSerializable(Key.ACTIVITY_LEVEL, activityLevel);
                mFitnessInputBundle.putInt(Key.HEIGHT, height);
                mFitnessInputBundle.putInt(Key.WEIGHT, weight);
                mFitnessInputBundle.putSerializable(Key.WEIGHT_GOAL, delta);

                /* Important! onDataPass may also be called from onWarningAccepted. Control flow is
                 * diverted to onWarningAccepted if the user's weight goal is unhealthy. The user is
                 * then given the decision whether to proceed against the warning or return to change
                 * their weight goal.
                 */
                mDataPasser.onDataPass(FITNESS_INPUT_FRAGMENT, mFitnessInputBundle);

                break;
        }
    }

    @Override
    public void onWarningAccepted() {
        mDataPasser.onDataPass(FITNESS_INPUT_FRAGMENT, mFitnessInputBundle);
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
        // Do nothing.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    /**
     * An interface to communicate with this Fragment's host Activity.
     */
    public interface OnDataPass {
        void onDataPass(String key, Bundle signInBundle);
    }

    // call this first
    private void initializeUnitsToLocale(CountryCode countryCode) {
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

    // Current implementation there is a default button selected so no way for this to return null
    private Sex getRadioSelectedSex(int buttonId) {
        return buttonId == R.id.rb_male ? Sex.MALE : Sex.FEMALE;
    }

    // Current implementation there is a default button selected so no way for this to return null
    private ActivityLevel getRadioSelectedActivityLevel(int buttonId) {

        ActivityLevel activityLevel = ActivityLevel.SEDENTARY;
        if (buttonId == R.id.rb_moderately_active) {
            activityLevel = ActivityLevel.MODERATELY_ACTIVE;
        } else if (buttonId == R.id.rb_active) {
            activityLevel = ActivityLevel.ACTIVE;
        } // Additional activity levels may be added in the future.

        return activityLevel;
    }

    private boolean isUnhealthyGoal(int weightDelta) {
        return Math.abs(weightDelta) > mUnhealthyDelta;
    }

    private void displayDialog(String tag) {
        if (tag == UNHEALTHY_GOAL_DIALOG) {
            loadDialogFragment(new WarningDialogFragment(), UNHEALTHY_GOAL_DIALOG, false);
        }
    }

    private void loadDialogFragment(DialogFragment dialogFragment, String tag, boolean addToBackStack) {
        ReusableUtil.loadDialogFragment(getFragmentManager(), dialogFragment, FitnessInputFragment.this, tag, addToBackStack);
    }
}
