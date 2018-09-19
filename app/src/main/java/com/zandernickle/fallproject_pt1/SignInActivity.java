package com.zandernickle.fallproject_pt1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private static final String AGE_LABEL = "Age";
    private static final String COUNTRY_LABEL = "Country";

    private static final int KEY = 0;
    private static final int EDIT_TEXT = 1;

    // "*" According to Material Design guidelines.
    private static final String REQUIRED_FIELD = "*Required";

    public static final int MIN_AGE = 13;
    public static final int MAX_AGE = 120;

    TextView mTvSignUp;
    ImageButton mImgBtnAddProfileImage;
    ImageView mImgViewProfileImage;
    CustomTextInputLayout mTilName, mTilPostalCode;
    AppCompatSpinner mSpinAge, mSpinCountry;
    Button mBtnSubmit;

    HashMap<String, CustomTextInputLayout> mTextInputLayouts;
    Bitmap mBitmapProfileImg;
    Boolean mHasCompleteData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvSignUp = findViewById(R.id.tv_sign_up);
        mImgBtnAddProfileImage = findViewById(R.id.ib_add_profile_image);
        mImgViewProfileImage = findViewById(R.id.iv_profile_image);
        mTilName = findViewById(R.id.til_name);
        mTilPostalCode = findViewById(R.id.til_postal_code);
        mSpinAge = findViewById(R.id.spinner_age);
        mSpinCountry = findViewById(R.id.spinner_country);
        mBtnSubmit = findViewById(R.id.button_submit);

        initializeMapTextInputLayouts();
        initializeSpinners();

        ReusableUtil.setOnClickListeners(SignInActivity
                .this, mImgBtnAddProfileImage, mBtnSubmit);
        ReusableUtil.setOnClickListeners(SignInActivity
                .this, mTilName.getEditText());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // TODO
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO
    }

    /**
     * Initializes the HashMap containing all TextInputLayouts for SignInActivity. Use this method
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
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(SignInActivity
                .this,
                R.layout.spinner_item, spinnerAgeData);
        // Set dropdown View resource here.
        mSpinAge.setAdapter(ageAdapter);

        String[] spinnerCountryData = SpinnerUtil.getSpinnerCountryData();
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(SignInActivity
                .this,
                R.layout.spinner_item, spinnerCountryData);
        // Set dropdown View resource here.
        mSpinCountry.setAdapter(countryAdapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ib_add_profile_image:
                ReusableUtil.attemptImageCapture(SignInActivity
                        .this, Key.REQUEST_IMAGE_CAPTURE);
                break;

            case R.id.button_submit:

                String[][] editTextData = {
                        new String[] { Key.NAME, mTilName.getEditTextString() },
                        new String[] { Key.POSTAL_CODE, mTilPostalCode.getEditTextString() }
                };

                String age = mSpinAge.getSelectedItem().toString();
                String country = mSpinCountry.getSelectedItem().toString();

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

                if (age == AGE_LABEL || country == COUNTRY_LABEL) {
                    mHasCompleteData = false;
                    // TODO: Error/Required messages
                }

                if (mHasCompleteData) {
                    Bundle userAccountBundle = new Bundle();
                    ReusableUtil.bitmapToBundle(userAccountBundle, mBitmapProfileImg, Key.PROFILE_IMAGE);
                    userAccountBundle.putString(Key.NAME, mTilName.getEditText().getText().toString());
                    userAccountBundle.putInt(Key.AGE, Integer.parseInt(age));
                    // TODO: Put postal code
                    userAccountBundle.putString(Key.COUNTRY, country);
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // TODO: Only pass resultCode to onImageCaptureResult; check request code here.

        if (requestCode == Key.REQUEST_IMAGE_CAPTURE) {
            // Nullable
            mBitmapProfileImg = ReusableUtil.onImageCaptureResult(SignInActivity
                            .this, data, resultCode, null);
            if (mBitmapProfileImg != null) {
                mImgBtnAddProfileImage.setVisibility(View.INVISIBLE);
                mImgViewProfileImage.setImageBitmap(mBitmapProfileImg);
                mImgViewProfileImage.setVisibility(View.VISIBLE);
            }

            /* TODO: Move back here?
             * If mBitmapProfileImg is null, mHasCompleteData is set to false when mBtnSubmit is
             * clicked.
             */
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Required override. Do nothing.
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mTilName.isErrorEnabled()) {
            mTilName.setErrorEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Required override. Do nothing.
    }
}
