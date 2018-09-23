package com.zandernickle.fallproject_pt1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import static com.zandernickle.fallproject_pt1.Key.FITNESS_INPUT_FRAGMENT;
import static com.zandernickle.fallproject_pt1.Key.MENU_BAR_FRAGMENT_MENU_PRESSED;
import static com.zandernickle.fallproject_pt1.Key.MENU_BAR_FRAGMENT_PROFILE_PRESSED;
import static com.zandernickle.fallproject_pt1.Key.SIGN_IN_FRAGMENT;
import static com.zandernickle.fallproject_pt1.ReusableUtil.loadFragment;

public class MainActivity extends CustomAppCompatActivity implements SignInFragment.OnDataPass,
        FitnessInputFragment.OnDataPass, MenuBarFragment.OnDataPass, RVAdapter.OnDataPass {

    public static final String CURRENT_FRAGMENT_TAG = "CURRENT_FRAGMENT_TAG";
    public static final String PLAYGROUND_TAG = "PLAYGROUND_TAG";
    public static final String HIKES_TAG = "HIKES_TAG";
    public static final String WEATHER_TAG = "WEATHER_TAG";
    public static final String USER = "USER";

    /* TODO
     *
     * (Zander)
     * BMRFragment should expect a CountryCode rather than a String. This is a safer approach and the Key documentation
     * has already been changed to reflect this.
     * Simply use CountryCode countryCode = (CountryCode) arguments.getSerializable(Key.COUNTRY) and then check whether
     * countryCode == CountryCode.US.
     *
     * (Matt)
     * Create XML layout resources for every phone screen's landscape mode. How do we ensure the landscape fragment is
     * not rendered when a tablet is in landscape orientation?
     *
     * (Sydney)
     * Decide whether to pass the CountryCode to the weather and hike Fragments for conversion to Celsius where
     * appropriate (maybe this is already taken care of based on GPS).
     *
     * (Matt)
     * Keep state when leaving the app and returning. Come back to same fragment if possible.
     */

    private static final DatabaseService database = new DatabaseService(); // A placeholder for a real database.

    private FragmentManager mFragmentManager = getSupportFragmentManager(); // Reusable throughout the application.
    private User mUser; // The current user (kind of like a Cookie).
    private boolean mIsTablet;

    private String mCurrentFragmentTag;

    /**
     * {@inheritDoc}
     * <p>
     * The entry point for this application. Loads a new SignInFragment which then starts a
     * cascade of information exchange between additional Fragments and this Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIsTablet = isTablet(); // Pass to individual modules.

        int viewId = mIsTablet ? R.id.fl_fragment_placeholder_tablet_right :
                R.id.fl_fragment_placeholder_phone;

        if (savedInstanceState == null) {

            mCurrentFragmentTag = SIGN_IN_FRAGMENT;
            loadFragment(mFragmentManager, viewId, new SignInFragment(), mCurrentFragmentTag, false);

        } else {

            mUser = savedInstanceState.getParcelable(USER);

            mCurrentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
            Fragment currentFragment = mFragmentManager.findFragmentByTag(mCurrentFragmentTag);
            loadFragment(mFragmentManager, viewId, currentFragment, mCurrentFragmentTag, false);

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // TODO: Why won't the code in onCreate work here?
        // There are no errors but no Fragment is rendered (blank screen).
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(USER, mUser);
        outState.putString(CURRENT_FRAGMENT_TAG, mCurrentFragmentTag);

    }

    /**
     * {@inheritDoc}
     * <p>
     * All communication with this Activity is via the OnDataPass interface. Though the interfaces
     * all share the same name, all interface hosts are registered in this Activity's class signature.
     * The origin of incoming data is identifiable by the passing of a key through the onDataPass
     * method signature.
     */
    @Override
    public void onDataPass(String key, Bundle bundle) { // could implement this without the key but then we have to unpack a bundle every time we tap on the menu icon (check if menu icon or profile)

        // TODO: Decide how often to update the database. // onPause?

        switch (key) {

            case SIGN_IN_FRAGMENT:
                mUser = new User(bundle);
                database.addUser(mUser);

                /*
                 * At this point the user's account has been created but their health data has not been
                 * appended to their profile. If they choose not to proceed beyond the FitnessInputFragment
                 * their account will remain.
                 *
                 * There are currently no checks to prevent duplicate accounts. Nor are there options to
                 * remove an account once it has been created (apart from destroying this Activity of course).
                 */

                Fragment signInFragment = mFragmentManager.findFragmentByTag(SIGN_IN_FRAGMENT);
                Fragment fitnessInputFragment = new FitnessInputFragment();
                fitnessInputFragment.setArguments(bundle);

                mCurrentFragmentTag = FITNESS_INPUT_FRAGMENT;
                loadFragment(mFragmentManager, signInFragment.getId(), fitnessInputFragment, mCurrentFragmentTag, false);

                break;

            case FITNESS_INPUT_FRAGMENT:

                mUser.updateFitnessData(bundle);
                database.updateUser(mUser);

                /*
                 * At this point the user's profile has been updated with their basic fitness information (see the
                 * User.updateFitnessData method). However, their BMI and BMR have yet to be calculated. These are
                 * added when the BMR fragment returns its data.
                 */

                if (isTablet()) { // Sets up MasterView on the left for the remainder of the experience
                    loadFragment(mFragmentManager, R.id.fl_fragment_placeholder_tablet_left, new MasterListFragment(), "TEST", false);
                }

                // TESTS ........

                Bundle testBundle = new Bundle();
                testBundle.putSerializable(Key.MODULE, Module.HEALTH);
                testBundle.putByteArray(Key.PROFILE_IMAGE, mUser.getProfileImage());
                testBundle.putBoolean(Key.IS_TABLET, mIsTablet);

                Fragment prevFitnessInputFragment = mFragmentManager.findFragmentByTag(FITNESS_INPUT_FRAGMENT);
                Fragment playgroundFragment = new PlaygroundFragment();
                playgroundFragment.setArguments(testBundle);

                mCurrentFragmentTag = PLAYGROUND_TAG;
                loadFragment(mFragmentManager, prevFitnessInputFragment.getId(), playgroundFragment, mCurrentFragmentTag, false);

                // End tests ........

                break;

            case MENU_BAR_FRAGMENT_MENU_PRESSED: // Ignore the Bundle, its null
//                ReusableUtil.toast(this, "Load the RecyclerView");

//                Fragment frag = mFragmentManager.findFragmentByTag(mCurrentFragmentTag);
//                Fragment master = new MasterListFragment();
//                mCurrentFragmentTag = "test";
//                loadFragment(mFragmentManager, frag.getId(), master, mCurrentFragmentTag, false);

                // Test HikesFragment
                Bundle testHikeBundle = new Bundle();
                testHikeBundle.putSerializable(Key.MODULE, Module.HIKES);
                testHikeBundle.putByteArray(Key.PROFILE_IMAGE, mUser.getProfileImage());
                testHikeBundle.putBoolean(Key.IS_TABLET, mIsTablet);

                Fragment prevFragment = mFragmentManager.findFragmentByTag(mCurrentFragmentTag);
                Fragment hikesFragment = new HikesFragment();
                hikesFragment.setArguments(testHikeBundle);

                mCurrentFragmentTag = HIKES_TAG;
                loadFragment(mFragmentManager, prevFragment.getId(), hikesFragment, mCurrentFragmentTag, false);

                break;

            case MENU_BAR_FRAGMENT_PROFILE_PRESSED: // Ignore the Bundle, its null
//                ReusableUtil.toast(this, "Load the UpdateProfileFragment");

                // Test WeatherFragment
                Bundle testWeatherBundle = new Bundle();
                testWeatherBundle.putSerializable(Key.MODULE, Module.WEATHER);
                testWeatherBundle.putByteArray(Key.PROFILE_IMAGE, mUser.getProfileImage());
                testWeatherBundle.putBoolean(Key.IS_TABLET, mIsTablet);

                Fragment prevFragment2 = mFragmentManager.findFragmentByTag(mCurrentFragmentTag);
                Fragment weatherFragment = new WeatherFragment();
                weatherFragment.setArguments(testWeatherBundle);

                mCurrentFragmentTag = WEATHER_TAG;
                loadFragment(mFragmentManager, prevFragment2.getId(), weatherFragment, mCurrentFragmentTag, false);

                break;
        }

        // Track previous fragment here.
    }
}

