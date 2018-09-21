package com.zandernickle.fallproject_pt1;

/**
 * Constants used to pass data throughout the app. Append to this list any keys (or tags/ids) not
 * bounded by a single class.
 */
public class Key {

    // SignInFragment <-> MainActivity <-> FitnessInputFragment
    public static final String NAME = "NAME"; // String
    public static final String PROFILE_IMAGE = "PROFILE_IMAGE"; // String
    public static final String AGE = "AGE"; // int
    public static final String POSTAL_CODE = "POSTAL_CODE"; // int
    public static final String COUNTRY = "COUNTRY"; // enum (CountryCode)

    // FitnessInputFragment <-> MainActivity <-> BMRFragment
    public static final String HEIGHT = "HEIGHT"; // int (units defined by Key.COUNTRY)
    public static final String WEIGHT = "WEIGHT"; // int (units defined by Key.COUNTRY)
    public static final String SEX = "SEX"; // enum (Sex)
    public static final String ACTIVITY_LEVEL = "ACTIVITY_LEVEL"; // enum (ActivityLevel)
    public static final String GOAL = "GOAL"; // enum (Goal)
    public static final String WEIGHT_GOAL = "WEIGHT_GOAL"; // int (units defined by Key.COUNTRY)

    // TODO:
    public static final String BMR = "BMR"; //int (num kCal per day a person needs to maintain weight)
    public static final String BMI = "BMI"; //int

    // Used as both keys & tags
    public static final String SIGN_IN_FRAGMENT = "SIGN_IN_FRAGMENT";
    public static final String FITNESS_INPUT_FRAGMENT = "FITNESS_INPUT_FRAGMENT";

    // Misc
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String AGE_SPINNER_LABEL = "Age";
    public static final String COUNTRY_SPINNER_LABEL = "Country";
}
