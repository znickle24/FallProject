package com.zandernickle.fallproject_pt1;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.neovisionaries.i18n.CountryCode;

import java.util.HashMap;

import static com.zandernickle.fallproject_pt1.ReusableUtil.attemptImageCapture;
import static com.zandernickle.fallproject_pt1.ReusableUtil.disableTILErrorOnTextChanged;
import static com.zandernickle.fallproject_pt1.ReusableUtil.onImageCaptureResult;
import static com.zandernickle.fallproject_pt1.ReusableUtil.setOnClickListeners;
import static com.zandernickle.fallproject_pt1.ReusableUtil.setTextChangedListeners;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private static final int KEY = 0;
    private static final int EDIT_TEXT = 1;

    // "*" According to Material Design guidelines.
    private static final String REQUIRED_FIELD = "*Required";

    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 120;

    private TextView mTvSignUp;
    private ImageButton mImgBtnAddProfileImage;
    private ImageView mImgViewProfileImage;
    private CustomTextInputLayout mTilName, mTilPostalCode;
    private AppCompatSpinner mSpinAge, mSpinCountry;
    private Button mBtnSubmit;

    private HashMap<String, CustomTextInputLayout> mTextInputLayouts;
    private Bitmap mBitmapProfileImg;
    private Boolean mHasCompleteData = true;

    private OnDataPass mDataPasser;

    public SignInFragment() {
        // Required empty public constructor
    }

    public interface OnDataPass {
        void onDataPass(String key, Bundle signInBundle);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mDataPasser = (OnDataPass) context;
        } catch (ClassCastException e) {
            String message = context.toString() + " must implement " +
                    SignInFragment.class.toString() + ".OnDataPass";
            throw new ClassCastException(message);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisFragment = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mTvSignUp = thisFragment.findViewById(R.id.tv_sign_up);
        mImgBtnAddProfileImage = thisFragment.findViewById(R.id.ib_add_profile_image);
        mImgViewProfileImage = thisFragment.findViewById(R.id.iv_profile_image);
        mTilName = thisFragment.findViewById(R.id.til_name);
        mTilPostalCode = thisFragment.findViewById(R.id.til_postal_code);
        mSpinAge = thisFragment.findViewById(R.id.spinner_age);
        mSpinCountry = thisFragment.findViewById(R.id.spinner_country);
        mBtnSubmit = thisFragment.findViewById(R.id.button_submit);

        initializeMapTextInputLayouts();
        initializeSpinners();

        setOnClickListeners(SignInFragment.this, mImgBtnAddProfileImage, mBtnSubmit);
        setTextChangedListeners(SignInFragment.this, mTilName.getEditText(), mTilPostalCode.getEditText());

        return thisFragment;
    }

    /**
     * Initializes the HashMap containing all TextInputLayouts for SignInFragment. Use this method
     * to initialize any future additions of TextInputLayout.
     */
    private void initializeMapTextInputLayouts() {
        mTextInputLayouts = new HashMap<String, CustomTextInputLayout>() {{
            put(Key.NAME, mTilName);
            put(Key.POSTAL_CODE, mTilPostalCode);
        }};
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
        // Set dropdown View resource here.
        mSpinAge.setAdapter(ageAdapter);

        String[] spinnerCountryData = SpinnerUtil.getSpinnerCountryData();
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(SignInFragment.this.getActivity(),
                R.layout.spinner_item, spinnerCountryData);
        // Set dropdown View resource here.
        mSpinCountry.setAdapter(countryAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_add_profile_image:
                attemptImageCapture(SignInFragment.this, Key.REQUEST_IMAGE_CAPTURE);
                break;

            case R.id.button_submit:

                String[][] editTextData = {
                        new String[]{Key.NAME, mTilName.getTextString()},
                        new String[]{Key.POSTAL_CODE, mTilPostalCode.getTextString()}
                };

                String age = mSpinAge.getSelectedItem().toString();

                CountryCode countryCode = getCountryCodeFromSpinner(mSpinCountry);
                if (countryCode ==  null) {
                    mHasCompleteData = false;
                    // TODO: Provide user feedback.
                }

                if (mBitmapProfileImg == null) {
                    mHasCompleteData = false;
                    // TODO: Provide user feedback.
                }

                for (String[] data : editTextData) {
                    if (data[EDIT_TEXT].length() == 0) {
                        mHasCompleteData = false;
                        mTextInputLayouts.get(data[KEY]).showError(REQUIRED_FIELD);
                    }
                }

                if (age == Key.AGE_SPINNER_LABEL) {
                    mHasCompleteData = false;
                    // TODO: Error/Required messages
                }

                // TODO: First & Last name conversion
//                if (mHasCompleteData) {
                Bundle signInBundle = new Bundle();
//                    bitmapToBundle(signInBundle, mBitmapProfileImg, Key.PROFILE_IMAGE);
                signInBundle.putString(Key.NAME, mTilName.getEditText().getText().toString());
                signInBundle.putInt(Key.AGE, 18);
                signInBundle.putInt(Key.POSTAL_CODE, 123456);
                signInBundle.putSerializable(Key.COUNTRY, countryCode);

                mDataPasser.onDataPass(Key.SIGN_IN_FRAGMENT, signInBundle);

//                }

                break;
            default:
                break;
        }
    }

    // TODO: Move most of onClick to its own method.

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Key.REQUEST_IMAGE_CAPTURE) {
            // Nullable
            mBitmapProfileImg = onImageCaptureResult(SignInFragment.this.getActivity()
                    , data, resultCode, null);
            if (mBitmapProfileImg != null) {
                mImgBtnAddProfileImage.setVisibility(View.INVISIBLE);
                mImgViewProfileImage.setImageBitmap(mBitmapProfileImg);
                mImgViewProfileImage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mTilName.isErrorEnabled() || mTilPostalCode.isErrorEnabled()) {
            disableTILErrorOnTextChanged(s.hashCode(), mTilName, mTilPostalCode);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Nullable
    private CountryCode getCountryCodeFromSpinner(Spinner countrySpinner) {
        String selectedItemString = countrySpinner.getSelectedItem().toString();
        String alpha2Code = selectedItemString.split("\\s+")[0]; // "US - United States"
        return CountryCode.getByCode(alpha2Code);
    }
}
