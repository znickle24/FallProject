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

        mSignInViewModel = ViewModelProviders.of(this).get(SignInViewModel.class);

        mTextInputLayoutMap = mapTextInputLayouts(mTilName, mTilPostalCode);
        initializeSpinners();

        mTilName.getEditText().setText(mSignInViewModel.name);
        mTilPostalCode.getEditText().setText(mSignInViewModel.postalCode);

        byte[] compressedImage = mSignInViewModel.profileImage;
        if (compressedImage != null) {
            // TODO: Simplify
            mBitmapProfileImg = ReusableUtil.byteArrayToBitmap(compressedImage);
            mImgViewProfileImage.setImageBitmap(mBitmapProfileImg);
            mImgBtnAddProfileImage.setVisibility(View.INVISIBLE);
            mImgViewProfileImage.setImageBitmap(mBitmapProfileImg);
            mImgViewProfileImage.setVisibility(View.VISIBLE);
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
        // the edit texts are saved elsewhere & spinners keep state
        if (mBitmapProfileImg != null) {
            mSignInViewModel.profileImage = ReusableUtil.bitmapToByteArray(mBitmapProfileImg);
        }
        mSignInViewModel.ageSpinPosition = mSpinAge.getSelectedItemPosition();
        mSignInViewModel.countrySpinPosition = mSpinCountry.getSelectedItemPosition();
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

                    User user = new User();
                    user.setName(name);
                    user.setAge(age);
                    user.setPostalCode(postalCode);
                    user.setCountryCode(countryCode);
                    user.setProfileImage(ReusableUtil.bitmapToByteArray(mBitmapProfileImg));

                    int resultCode = mSignInViewModel.addUserSync(user);
                    log("NEW USER ATTEMPTED ADD; ID: " + resultCode + " : NAME: " + user.getName());

                    if (resultCode == UserRepository.NON_EXISTENT_ID) {
                        mTilName.setErrorEnabled(true);
                        mTilName.setError("This name is already taken.");
                        break;
                    }

                    mSignInViewModel.updateActiveUser(resultCode);
                    user.setId(resultCode);
                    signInBundle.putParcelable(Key.USER, user);
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
        if (s.hashCode() == mTilName.getEditText().getText().hashCode()) {
            mSignInViewModel.name = s.toString();
        } else {
            mSignInViewModel.postalCode = s.toString();
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
        } else if (parentId == R.id.spin_country) {
            mTvCountryRequiredLabel.setText("");
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
