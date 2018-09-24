package com.zandernickle.fallproject_pt1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import static com.zandernickle.fallproject_pt1.Key.SIGN_IN_FRAGMENT;
import static com.zandernickle.fallproject_pt1.ReusableUtil.loadFragment;
import static com.zandernickle.fallproject_pt1.ReusableUtil.log;

public class MainActivity extends CustomAppCompatActivity implements SignInFragment.OnDataPass,
        FitnessInputFragment.OnDataPass, MenuBarFragment.OnDataPass, RVAdapter.OnDataPass {

    public static final String PREV_FRAGMENT_TAG = "PREV_FRAGMENT_TAG";
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
    private Bundle mBundle;

    private String mPrevFragmentTag;

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

        mBundle = new Bundle();
        mBundle.putBoolean(Key.IS_TABLET, mIsTablet);

        int viewId = mIsTablet ? R.id.fl_fragment_placeholder_tablet_right :
                R.id.fl_fragment_placeholder_phone;

        if (savedInstanceState == null) {

            mPrevFragmentTag = SIGN_IN_FRAGMENT;
            loadFragment(mFragmentManager, viewId, new SignInFragment(), mPrevFragmentTag, false);

        } else {

            mUser = savedInstanceState.getParcelable(USER);

            mPrevFragmentTag = savedInstanceState.getString(PREV_FRAGMENT_TAG);
            Fragment currentFragment = mFragmentManager.findFragmentByTag(mPrevFragmentTag);
            loadFragment(mFragmentManager, viewId, currentFragment, mPrevFragmentTag, false);

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
        outState.putString(PREV_FRAGMENT_TAG, mPrevFragmentTag);

    }

    private Fragment getNextFragmentToLoad(Module moduleToLoad) {
        HashMap<Module, Class<?>> mappedModules = ReusableUtil.mapModuleList(); // get the class corresponding with the module (a fragment)
        Fragment nextFragment = null;
        try {
            Class<?> moduleClass = mappedModules.get(moduleToLoad); // the class corresponding with the module (a fragment)
            Constructor<?> constructor = Class.forName(moduleClass.getName()).getConstructor();
            nextFragment = (Fragment) constructor.newInstance();
        } catch (Exception e) {
            log(e.getMessage());
            log("Failed to retrieve next loadable fragment: " + moduleToLoad.toString());
            e.printStackTrace();
        }
        return nextFragment; // nullable but should never return null?
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
    public void onDataPass(Module moduleToLoad, Bundle bundle) { // could implement this without the key but then we have to unpack a bundle every time we tap on the menu icon (check if menu icon or profile)

        // TODO: Decide how often to update the database. // onPause?

        // Create the next fragment but just keep adding things to the bundle (may need to store as member variable for lifecycle)
        if (bundle != null) {
            mBundle.putAll(bundle);
        } // some modules may return a null bundle (these just want you to know they're returning).
        mBundle.putSerializable(Key.MODULE, moduleToLoad); // Pass the name of the next module to the menu bar

        Fragment prevFragment = mFragmentManager.findFragmentByTag(mPrevFragmentTag); // replace hard-coded module
        Fragment nextFragment = getNextFragmentToLoad(moduleToLoad); // replace hard-coded module

        mPrevFragmentTag = moduleToLoad.toString(); // make sure this is placed after finding fragment by tag

        // update mBundle and load new fragment within each individual case

        switch (moduleToLoad) {

            case SIGN_IN:

                // Not implemented. This module is currently the entry point. See onCreate.
                break;

            case FITNESS_INPUT: // Returned from SignIn module

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

                break;

            case PLAYGROUND:

                mUser.updateFitnessData(bundle);
                database.updateUser(mUser);

                /*
                 * At this point the user's profile has been updated with their basic fitness information (see the
                 * User.updateFitnessData method). However, their BMI and BMR have yet to be calculated. These are
                 * added when the BMR fragment returns its data.
                 */

                if (isTablet()) { // Sets up MasterView on the left for the remainder of the experience
                    Fragment masterListFragment = new MasterListFragment();
                    masterListFragment.setArguments(mBundle);
                    loadFragment(mFragmentManager, R.id.fl_fragment_placeholder_tablet_left, masterListFragment, "TEST", false);
                }

                break;

            case MASTER_LIST:
                break;

            case HIKES:
                break;

            case WEATHER:
                break;

            default:
                break;

        }

        nextFragment.setArguments(mBundle);
        loadFragment(mFragmentManager, prevFragment.getId(), nextFragment, mPrevFragmentTag, false);
    }
}

