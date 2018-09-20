package com.zandernickle.fallproject_pt1;

public class Key {

    /* MATT
     *
     * The following keys represent any data which might be passed from the on-boarding
     * activity(s) and/or the profile/account fragment. I've included a comment for each com.zandernickle.fallproject_pt1.Key
     * specifying the type of data to expect when retrieving the key's corresponding value. See
     * the enum files (or autocomplete) for all possible enum values.
     */
    public static final String NAME = "NAME"; // String
    public static final String PROFILE_IMAGE = "PROFILE_IMAGE"; // String
    public static final String AGE = "AGE"; // int
    public static final String POSTAL_CODE = "POSTAL_CODE"; // String
    public static final String COUNTRY = "COUNTRY"; // String
    public static final String HEIGHT = "HEIGHT"; // int (inches)
    public static final String WEIGHT = "WEIGHT"; // int (lbs)
    public static final String SEX = "SEX"; // enum (com.zandernickle.fallproject_pt1.Sex)
    public static final String ACTIVITY_LEVEL = "ACTIVITY_LEVEL"; // enum (com.zandernickle.fallproject_pt1.ActivityLevel)
    public static final String GOAL = "GOAL"; // enum (com.zandernickle.fallproject_pt1.Goal)
    public static final String WEIGHT_GOAL = "WEIGHT_GOAL"; //int (lbs or kg)
    public static final String BMR = "BMR"; //int (num kCal per day a person needs to maintain weight)
    public static final String BMI = "BMI"; //int

    /* MATT
     *
     * Reusable constants.
     */
    public static final int REQUEST_IMAGE_CAPTURE = 1;

}
