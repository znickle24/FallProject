package com.zandernickle.fallproject_pt1;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.i18n.CountryCode;

import java.util.HashMap;

import static com.zandernickle.fallproject_pt1.ReusableUtil.attemptImageCapture;
import static com.zandernickle.fallproject_pt1.ReusableUtil.bitmapToBundle;
import static com.zandernickle.fallproject_pt1.ReusableUtil.bitmapToByteArray;
import static com.zandernickle.fallproject_pt1.ReusableUtil.disableTILErrorOnTextChanged;
import static com.zandernickle.fallproject_pt1.ReusableUtil.log;
import static com.zandernickle.fallproject_pt1.ReusableUtil.mapTextInputLayouts;
import static com.zandernickle.fallproject_pt1.ReusableUtil.onImageCaptureResult;
import static com.zandernickle.fallproject_pt1.ReusableUtil.setOnClickListeners;
import static com.zandernickle.fallproject_pt1.ReusableUtil.setOnItemSelectedListeners;
import static com.zandernickle.fallproject_pt1.ReusableUtil.setTextChangedListeners;
import static com.zandernickle.fallproject_pt1.ReusableUtil.toast;

/**
 * The first functional View rendered (vs. splash screen). Sets up a user's profile data
 * and sends the data to Main in order to create a new user profile and add the user's
 * data to the database.
 */
public class SignInFragment extends Fragment implements View.OnClickListener, TextWatcher, AppCompatSpinner.OnItemSelectedListener {

    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 120;
    private static final int SPINNER_LABEL_INDEX = 0;
    private static final String REQUIRED_FIELD = "*Required"; // "*" According to Material Design guidelines.

    private TextView mTvAgeRequiredLabel, mTvCountryRequiredLabel;
    private ImageButton mImgBtnAddProfileImage;
    private ImageView mImgViewProfileImage;
    private CustomTextInputLayout mTilName, mTilPostalCode;
    private AppCompatSpinner mSpinAge, mSpinCountry;
    private Button mBtnSubmit;

    private HashMap<Integer, TextInputLayout> mTextInputLayoutMap;
    private Bitmap mBitmapProfileImg;

    private OnDataPass mDataPasser;
    private SignInViewModel mSignInViewModel;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log("ON CREATE HAS BEEN CALLED");

        mSignInViewModel = ViewModelProviders.of(this).get(SignInViewModel.class);
        mSignInViewModel.getCount().observe(this, new Observer<Integer>() {

            @Override
            public void onChanged(@Nullable Integer count) {
                ReusableUtil.log("UPDATED COUNT: " + count);
            }
        });

        LiveData<User> user = mSignInViewModel.getUser(3);
        if (user.getValue() != null) {
            log("USER EXISTS: " + user.getValue());

            user.observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    ReusableUtil.log("USER CHANGED: " + user.getName() + ", id: " + user.getId());
                }
            });

        } else {
            log("USER DOES NOT EXIST: " + user.getValue());
        }

    }

    /**
     * {@inheritDoc}
     * <p>
     * Attaches this Fragment's OnDataPass interface to the host Activity (Main).
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mDataPasser = (OnDataPass) context;
        } catch (ClassCastException e) {
            // This should never happen but if it does, its sure nice to have a decent error message.
            String message = context.toString() + " must implement " + SignInFragment.class.toString() + ".OnDataPass";
            throw new ClassCastException(message);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Initializes or updates non-graphical member variables and updates some graphical
     * elements.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTextInputLayoutMap = mapTextInputLayouts(mTilName, mTilPostalCode);
        initializeSpinners();

        final String USER_ID = "USER_ID"; // TODO: This is temporary...

        EditText etName = mTextInputLayoutMap.get(R.id.til_name).getEditText();
        EditText etPostalCode = mTextInputLayoutMap.get(R.id.til_postal_code).getEditText();

        if (etPostalCode == null) {
            ReusableUtil.toast(getContext(), "failed");
        }

//        mSignInViewModel = ViewModelProviders.of(this).get(SignInViewModel.class);

        mSignInViewModel.getName().observe(this, new ObserverUtil.EditTextObserver(etName));
        mSignInViewModel.getPostalCode().observe(this, new ObserverUtil.EditTextObserver(etPostalCode));
        mSignInViewModel.getAgeIndex().observe(this, new ObserverUtil.SpinnerObserver(mSpinAge));
        mSignInViewModel.getCountryCodeIndex().observe(this, new ObserverUtil.SpinnerObserver(mSpinCountry));

        // This may be the first time the user has accessed the app.
        if (getArguments().containsKey(USER_ID)) {

        }

//        if (savedInstanceState.getInt(USER_ID) != -1) {
//
//        }
//        mSignInViewModel = ViewModelProviders.of(this).get(SignInViewModel.class);
//        mSignInViewModel.init(savedInstanceState.getInt(USER_ID));
//        mSignInViewModel.getUser().observe(this, observer);


        if (savedInstanceState != null) {
            mTilName.getEditText().setText(savedInstanceState.getString(Key.NAME));
            mTilPostalCode.getEditText().setText(savedInstanceState.getString(Key.POSTAL_CODE));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Does the usual inflation stuff and sets up any action listeners.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisFragment = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mTvAgeRequiredLabel = thisFragment.findViewById(R.id.tv_age_required);
        mTvCountryRequiredLabel = thisFragment.findViewById(R.id.tv_country_required);
        mImgBtnAddProfileImage = thisFragment.findViewById(R.id.iv_add_profile_image);
        mImgViewProfileImage = thisFragment.findViewById(R.id.civ_profile_container);
        mTilName = thisFragment.findViewById(R.id.til_name);
        mTilPostalCode = thisFragment.findViewById(R.id.til_postal_code);
        mSpinAge = thisFragment.findViewById(R.id.spin_age);
        mSpinCountry = thisFragment.findViewById(R.id.spin_country);
        mBtnSubmit = thisFragment.findViewById(R.id.button_submit);

        setOnClickListeners(SignInFragment.this, mImgBtnAddProfileImage, mBtnSubmit);
        setTextChangedListeners(SignInFragment.this, mTilName.getEditText(), mTilPostalCode.getEditText());
        setOnItemSelectedListeners(SignInFragment.this, mSpinAge, mSpinCountry);

//        ReusableUtil.setPortraitOnly(getActivity(), getArguments().getBoolean(Key.IS_TABLET));

        return thisFragment;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Key.NAME, mTilPostalCode.getTextString());
        outState.putString(Key.POSTAL_CODE, mTilPostalCode.getTextString());
    }


    /**
     * {@inheritDoc}
     * <p>
     * Register views in onCreateView via ReusableUtil.setOnClickListeners.
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_add_profile_image:
                attemptImageCapture(SignInFragment.this, Key.REQUEST_IMAGE_CAPTURE);
                break;

            case R.id.button_submit:

                if (ensureCompletedFields()) {

                    String name = mTilName.getTextString();
                    int age = Integer.parseInt(mSpinAge.getSelectedItem().toString());
                    int postalCode = Integer.parseInt(mTilPostalCode.getTextString());
                    CountryCode countryCode = getCountryCodeFromSpinner(mSpinCountry);

                    Bundle signInBundle = new Bundle();

                    bitmapToBundle(signInBundle, mBitmapProfileImg, Key.PROFILE_IMAGE);
                    signInBundle.putString(Key.NAME, name);
                    signInBundle.putInt(Key.AGE, age);
                    signInBundle.putInt(Key.POSTAL_CODE,postalCode);
                    signInBundle.putSerializable(Key.COUNTRY, countryCode);




//                    byte[] compressedImage = bitmapToByteArray(mBitmapProfileImg);
//                    User user = new User(name, compressedImage, age, postalCode, countryCode);

//                   int id, Sex sex, ActivityLevel activityLevel, int height, int weight, int delta
//                    user.setSex(Sex.FEMALE);
//                    user.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
//                    user.setHeight(4);
//                    user.setWeight(3);
//                    user.setWeightGoal(1);

                    // int id, int BMI, int BMR, int calorieIntake
//                    user.setBMI(100);
//                    user.setBMR(2000);
//                    user.setCalorieIntake(250);

//                    ActiveUser activeUser = new ActiveUser();
//                    activeUser.setUserId(1);


//                    log("\n");
////                    log("\n");
////                    log("\nPRE-DATABASE ACCESS");
////                    log("" + user.getId());
////                    log(user.getName());
////
//                    int testId = mSignInViewModel.addUser(user);
//                    mSignInViewModel.updateActiveUser(testId);
//                    if (testId != UserRepository.ABORT) {
//                        user.setId(testId);
//                        log("\nPOST-DATABASE ACCESS");
//                        log("" + user.getId());
//                        log(user.getName());
//                    } else {
//                        log("FAILED TO ADD USER");
//                    }
////
////                    log("\n");
////                    log("\n");


//                    int nonExistentUserId = mSignInViewModel.getActiveUser();
//                    log("NON EXISTENT USER ID: " + nonExistentUserId);

//                    int userId = mSignInViewModel.addUser(user);
//                    user.setId(userId);
//                    if (userId != -1) {
//                        ActiveUser activeUser = new ActiveUser();
//                        activeUser.setUserId(userId);
//                        mSignInViewModel.updateActiveUser(activeUser);
//                        int result = mSignInViewModel.getActiveUser();
//                        log("RESULT: " + result);
//                    }


                    mDataPasser.onDataPass(Module.FITNESS_INPUT, signInBundle); // SignInFragment -> MainActivity
                }

                break;

            default:
                break;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates the user's profile image with the most recent image capture result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Key.REQUEST_IMAGE_CAPTURE) {
            // Nullable
            mBitmapProfileImg = onImageCaptureResult(SignInFragment.this.getActivity(), data, mBitmapProfileImg, resultCode, null);
            if (mBitmapProfileImg != null) {

                // TODO: Add an update image button.

                mImgBtnAddProfileImage.setVisibility(View.INVISIBLE);
                mImgViewProfileImage.setImageBitmap(mBitmapProfileImg);
                mImgViewProfileImage.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Unimplemented. Required by TextWatcher.
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     * <p>
     * Implemented on behalf of TextInputEditText via TextWatcher. Removes the error message
     * from the active EditText when the user attempts to correct an error.
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mTilName.isErrorEnabled() || mTilPostalCode.isErrorEnabled()) {
            disableTILErrorOnTextChanged(s.hashCode(), mTilName, mTilPostalCode);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Unimplemented. Required by TextWatcher.
     */
    @Override
    public void afterTextChanged(Editable s) {
    }

    /**
     * {@inheritDoc}
     * <p>
     * Implemented on behalf of AppCompatSpinner. Removes the associated REQUIRED label
     * when a valid item is selected. Updates ViewModel.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        int parentId = parent.getId();

        if (parentId == R.id.spin_age) {
            mTvAgeRequiredLabel.setText("");
            mSignInViewModel.getAgeIndex().setValue(mSpinAge.getSelectedItemPosition());
        } else if (parentId == R.id.spin_country) {
            mTvCountryRequiredLabel.setText("");
            mSignInViewModel.getCountryCodeIndex().setValue(mSpinCountry.getSelectedItemPosition());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Unimplemented. Required by AppCompatSpinner.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    /**
     * An interface to communicate with this Fragment's host Activity.
     */
    public interface OnDataPass {
        void onDataPass(Module moduleToLoad, Bundle signInBundle);
    }

    /**
     * Initializes Spinner Views with the appropriate data. Use this method to add any additional
     * spinners not defined in the xml or those to which external or dynamic data should be added.
     * <p>
     * See SpinnerUtil.java for creating/retrieving Spinner data.
     */
    private void initializeSpinners() {

        // TODO: Will the adapter need to be stored as a member variable and re-initialized?

        String[] spinnerAgeData = SpinnerUtil.getSpinnerAgeData(MIN_AGE, MAX_AGE);
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(SignInFragment.this.getActivity(),
                R.layout.spinner_item, spinnerAgeData);
        // TODO: Set dropdown View resource here.
        mSpinAge.setAdapter(ageAdapter);

        String[] spinnerCountryData = SpinnerUtil.getSpinnerCountryData();
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(SignInFragment.this.getActivity(),
                R.layout.spinner_item, spinnerCountryData);
        // TODO: Set dropdown View resource here.
        mSpinCountry.setAdapter(countryAdapter);
    }

    private boolean ensureCompletedFields() {

        boolean hasCompletedFields = true;

        /* This piece of code is seemingly more complex than it needs to be for the current
         * implementation. However, this is designed for a balance between readability and
         * non-repetition while providing extensibility. Any additional conditions applicable to any
         * of the registered TextInputLayouts (or their child TextInputEditTexts) may be quickly
         * inserted into this for-loop.
         *
         * mTextInputLayoutMap is a HashMap containing TextInputLayouts where the key is each
         * items View.id.
         */
        for (int key : mTextInputLayoutMap.keySet()) {
            CustomTextInputLayout layout = ((CustomTextInputLayout) mTextInputLayoutMap.get(key));
            if (layout.getTextString().length() == 0) {
                hasCompletedFields = false;
                layout.showError(REQUIRED_FIELD);
            }
        }

        if (mSpinAge.getSelectedItemPosition() == SPINNER_LABEL_INDEX) {
            hasCompletedFields = false;
            mTvAgeRequiredLabel.setText(REQUIRED_FIELD); // Remove field in AdapterView.onItemSelected override.
        }

        if (mSpinCountry.getSelectedItemPosition() == SPINNER_LABEL_INDEX) {
            hasCompletedFields = false;
            mTvCountryRequiredLabel.setText(REQUIRED_FIELD); // Remove field in AdapterView.onItemSelected override.
        }

        if (mBitmapProfileImg == null) {
            hasCompletedFields = false;

            // TODO: Replace Toast with a custom DialogFragment (see WarningDialogFragment) or SnackBar.
            // TODO: Is there a best practice for showing an image is required? Why make it required?

            String message = "Hold on there. You forgot your profile picture.";
            Toast.makeText(SignInFragment.this.getContext(), message, Toast.LENGTH_LONG).show();
        }

        return hasCompletedFields;
    }

    /**
     * Returns the selected CountryCode from the registered Spinner. Returns null
     * if the code is invalid (the Spinner's label is selected).
     *
     * @param countrySpinner the Spinner containing the CountryCodes.
     * @return the CountryCode; null if invalid.
     */
    @Nullable
    private CountryCode getCountryCodeFromSpinner(Spinner countrySpinner) {
        String selectedItemString = countrySpinner.getSelectedItem().toString();
        String alpha2Code = selectedItemString.split("\\s+")[0]; // "US - United States" -> "US"
        return CountryCode.getByCode(alpha2Code); // "US" -> CountryCode
    }
}
