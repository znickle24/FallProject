package com.zandernickle.fallproject_pt1;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
 * Welcomes new users and asks for some basic fitness data in order to provide each user
 * with personalized BMI/BMR estimates. Shows locale-appropriate units such as pounds vs
 * kilograms and inches vs centimeters. Sends the fitness data to Main in order to update
 * the user's profile and display the BMR/BMI calculations (BMRFragment).
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
    private FitnessInputViewModel mViewModel;
    private User mUser;

    public FitnessInputFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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

        ReusableUtil.setPortraitOnly(getActivity(), getArguments().getBoolean(Key.IS_TABLET));

        return thisFragment;
    }

    /**
     * {@inheritDoc}
     *
     * Initializes or updates non-graphical member variables and updates some graphical
     * elements.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(FitnessInputViewModel.class);

        Bundle arguments = getArguments();
        mUser = arguments.getParcelable(Key.USER);

        String firstNameUppercase = getFirstNameUppercase(mUser.getName());
        mCountryCode = mUser.getCountryCode();

        mTvWelcome.setText(WELCOME + " " + firstNameUppercase);

        // The order is important here. See the docs.
        initializeUnitsToLocale(mCountryCode);
        initializeSeekBars();
        initializeSeekBarLabelValues();
    }

    /**
     * {@inheritDoc}
     *
     * Register views in onCreateView via ReusableUtil.setOnClickListeners.
     */
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

                mFitnessInputBundle = new Bundle();

                mFitnessInputBundle.putSerializable(Key.SEX, sex);
                mFitnessInputBundle.putSerializable(Key.ACTIVITY_LEVEL, activityLevel);
                mFitnessInputBundle.putInt(Key.HEIGHT, height);
                mFitnessInputBundle.putInt(Key.WEIGHT, weight);
                mFitnessInputBundle.putSerializable(Key.WEIGHT_GOAL, delta);

                mUser.setSex(sex);
                mUser.setActivityLevel(activityLevel);
                mUser.setHeight(height);
                mUser.setWeight(weight);
                mUser.setWeightGoal(delta);

                if (isUnhealthyGoal(delta)) {
                    /* The user's fitness data should not be updated if they select the dialog's back
                     * button (as would happen if onDataPass was immediately called).
                     *
                     * Pass a Bundle containing the CountryCode in order to choose which units to show
                     * in the Dialog's message. The decision to pass a bundle in place of only a CountryCode
                     * will facilitate future development when additional data may be required by the
                     * DialogFragment.
                     */
                    Bundle arguments = new Bundle();
                    arguments.putSerializable(Key.COUNTRY, mCountryCode);
                    displayDialog(UNHEALTHY_GOAL_DIALOG, arguments);
                } else {
                    /* Important! onDataPass may also be called from onWarningAccepted. Control flow is
                     * diverted to onWarningAccepted if the user's weight goal is unhealthy. The user is
                     * then given the decision whether to proceed against the warning or return to change
                     * their weight goal.
                     */
                    mViewModel.updateUser(mUser);
                    mDataPasser.onDataPass(Module.HEALTH, mFitnessInputBundle);
                }

                break;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Implemented on behalf of WarningDialogFragment.OnWarningAcceptListener.
     * Called when the user opts to accept the warning dialog and proceed to the
     * next task.
     */
    @Override
    public void onWarningAccepted() {
        mViewModel.updateUser(mUser);
        mDataPasser.onDataPass(Module.HEALTH, mFitnessInputBundle);
    }

    /**
     * {@inheritDoc}
     *
     * Implemented on behalf of SeekBar.OnSeekBarChangeListener. Updates the
     * corresponding labels to show the current progress of the SeekBar.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int seekBarId = seekBar.getId();
        if (seekBarId == R.id.sb_height) {
            mTvHeightResult.setText(seekBar.getProgress() + mUnitsHeight);
        } else if (seekBarId == R.id.sb_weight) {
            mTvWeightResult.setText(seekBar.getProgress() + mUnitsWeight);
        } else if (seekBarId == R.id.sb_weight_delta) {
            mTvWeightDeltaResult.setText(seekBar.getProgress() + " " + mUnitsWeight);
        } // Additional SeekBars could be added in the future.
    }

    /**
     * {@inheritDoc}
     *
     * Unimplemented. Required by SeekBar.OnSeekBarChangeListener.
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     *
     * Unimplemented. Required by SeekBar.OnSeekBarChangeListener.
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    /**
     * An interface to communicate with this Fragment's host Activity.
     */
    public interface OnDataPass {
        void onDataPass(Module moduleToLoad, Bundle signInBundle);
    }

    /**
     * Sets the appropriate units to the locale based on the provided CountryCode.
     *
     * Important! This method should be called before initializeSeekBars and
     * initializeSeekBarLabelValues.
     *
     * @param countryCode
     */
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

    /**
     * Sets the initial progress for each SeekBar when the View is first rendered. The
     * values defined here represent what the user will see before interacting with
     * any of the SeekBars.
     *
     * Call this method after initializeUnitsToLocale to ensure the locale-appropriate
     * units are shown.
     *
     * Important! This method should be called before initializeSeekBarLabelValues.
     * Otherwise the label values will not be updated with the intended progress.
     */
    private void initializeSeekBars() {
        mSbHeight.setBounds(mMinHeight, mMaxHeight);
        mSbWeight.setBounds(mMinWeight, mMaxWeight);
        mSbWeightDelta.setBounds(-mMaxDelta, mMaxDelta);

        mSbHeight.setProgress(mInitProgHeight);
        mSbWeight.setProgress(mInitProgWeight);
        mSbWeightDelta.setProgress(mInitProgWeightDelta);
    }

    /**
     * Sets the SeekBar label values to show the numerical value of the SeekBar's progress
     * when the View is first rendered.
     *
     * Call this method after initializeSeekBars.
     */
    private void initializeSeekBarLabelValues() {
        mTvHeightResult.setText(mSbHeight.getProgress() + " " + mUnitsHeight);
        mTvWeightResult.setText(mSbWeight.getProgress() + " " + mUnitsWeight);
        mTvWeightDeltaResult.setText(mSbWeightDelta.getProgress() + " " + mUnitsWeight);
        mTvWeightDeltaNeg.setText(NEG + mMaxDelta + " " + mUnitsWeight);
        mTvWeightDeltaPos.setText(POS + mMaxDelta + " " + mUnitsWeight);
    }

    /**
     * Returns a Sex enum representing the user's sex.
     *
     * The current implementation sets a default button to be selected. Therefore, the
     * return value should never be null.
     *
     * @param buttonId the button id of the currently selected radio button.
     * @return the user's sex.
     */
    @NonNull
    private Sex getRadioSelectedSex(int buttonId) {
        return buttonId == R.id.rb_male ? Sex.MALE : Sex.FEMALE;
    }

    /**
     * Returns an ActivityLevel enum representing the user's activity level.
     *
     * The current implementation sets a default button to be selected. Therefore, the
     * return value should never be null.
     *
     * @param buttonId the button id of the currently selected radio button.
     * @return the user's activity level.
     */
    @NonNull
    private ActivityLevel getRadioSelectedActivityLevel(int buttonId) {

        ActivityLevel activityLevel = ActivityLevel.SEDENTARY;
        if (buttonId == R.id.rb_moderately_active) {
            activityLevel = ActivityLevel.MODERATELY_ACTIVE;
        } else if (buttonId == R.id.rb_active) {
            activityLevel = ActivityLevel.ACTIVE;
        } // Additional activity levels may be added in the future.

        return activityLevel;
    }

    /**
     * Returns true if the weightDelta is greater than the calculated unhealthy weight delta.
     * See mUnhealthyDelta member variable initialization.
     *
     * Important! This is the weight change per week.
     *
     * @param weightDelta the user-specified weight change (neg, 0, or pos) per week.
     */
    private boolean isUnhealthyGoal(int weightDelta) {
        return Math.abs(weightDelta) > mUnhealthyDelta;
    }

    /**
     * Displays a DialogFragment as specified by the tag argument.
     *
     * The current implementation only contains a single DialogFragment, WarningDialogFragment.
     * However, this method was designed for the addition of future fragments. Simply create an
     * xml layout file, a custom DialogFragment class, and a local tag to specify which Dialog
     * to display.
     *
     * @param tag an identifier to determine which DialogFragment to display.
     * @param arguments optional data to pass to the DialogFragment.
     */
    private void displayDialog(String tag, Bundle arguments) {
        if (tag == UNHEALTHY_GOAL_DIALOG) {
            loadDialogFragment(new WarningDialogFragment(), arguments, UNHEALTHY_GOAL_DIALOG, false);
        }
    }

    /**
     * A wrapper for the ReusableUtil.loadDialogFragment method. This method simply ensures this
     * Fragment is the Dialog's host.
     *
     * @param dialogFragment the DialogFragment to load and render.
     * @param arguments optional data to pass to the DialogFragment.
     * @param tag  the tag to find the DialogFragment at a later point in time.
     * @param addToBackStack whether to add the DialogFragment to the Back Stack.
     */
    private void loadDialogFragment(DialogFragment dialogFragment, @Nullable Bundle arguments, String tag, boolean addToBackStack) {
        ReusableUtil.loadDialogFragment(getFragmentManager(), dialogFragment, FitnessInputFragment.this, arguments, tag, addToBackStack);
    }

    /**
     * Splits the name argument on whitespace and returns the String at index 0. This is
     * assumed to be the user's first name.
     *
     * Only call this method when name is guaranteed to be non-null.
     *
     * @param name the full name from which to extract the first name.
     * @return the user's first name.
     */
    private String getFirstNameUppercase(String name) {
        String firstName = name.split("\\s+")[0];
        return firstName.toUpperCase();
    }
}
