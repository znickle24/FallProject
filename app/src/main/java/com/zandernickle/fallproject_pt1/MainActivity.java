package com.zandernickle.fallproject_pt1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import static com.zandernickle.fallproject_pt1.Key.SIGN_IN_FRAGMENT;
import static com.zandernickle.fallproject_pt1.ReusableUtil.loadFragment;
import static com.zandernickle.fallproject_pt1.ReusableUtil.log;

public class MainActivity extends CustomAppCompatActivity implements SignInFragment.OnDataPass,
        FitnessInputFragment.OnDataPass, MenuBarFragment.OnDataPass, RVAdapter.OnDataPass, BMRFragment.OnDataPass {

    public static final String PREV_FRAGMENT_TAG = "PREV_FRAGMENT_TAG";
    private static final DatabaseService database = new DatabaseService(); // A placeholder for a real database.
    private static final HashMap<Module, Class<?>> mMappedModules = ReusableUtil.mapModuleList();

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private String mPrevFragmentTag;
    private User mUser;
    private Bundle mBundle;

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

        boolean isTablet = isTablet();
        int viewId = isTablet ? R.id.fl_fragment_placeholder_tablet_right :
                R.id.fl_fragment_placeholder_phone;

        if (savedInstanceState == null) {

            mBundle = new Bundle();
            mBundle.putBoolean(Key.IS_TABLET, isTablet);

            mPrevFragmentTag = SIGN_IN_FRAGMENT;
            loadFragment(mFragmentManager, viewId, new SignInFragment(), mPrevFragmentTag, false);

        } else {

            mBundle = savedInstanceState.getBundle(Key.BUNDLE);
            mUser = savedInstanceState.getParcelable(Key.USER);

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

        outState.putBundle(Key.BUNDLE, mBundle);
        outState.putParcelable(Key.USER, mUser);
        outState.putString(PREV_FRAGMENT_TAG, mPrevFragmentTag);

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
    public void onDataPass(Module moduleToLoad, Bundle bundle) {

        // TODO: Decide how often to update the database. // onPause?

        /*
         * For purposes of extensibility, all data is stored in a single Bundle. This Bundle is passed from one
         * module to the next, each extracting only the data it requires. Any Bundles passed into this interface
         * are added to this "global bundle".
         */

        if (bundle != null) {
            /* Some modules will return a null Bundle. For example, MenuBarFragment only passes the Module which
             * should next be loaded.
             */
            mBundle.putAll(bundle);
        }

        // Pass the name of the module to the MenuBarFragment (where implemented).
        mBundle.putSerializable(Key.MODULE, moduleToLoad);

        Fragment prevFragment = mFragmentManager.findFragmentByTag(mPrevFragmentTag);
        Fragment nextFragment = (Fragment) getNextModuleView(moduleToLoad);

        // Ensure this statement is made AFTER finding the previous Fragment.
        mPrevFragmentTag = moduleToLoad.toString();

        switch (moduleToLoad) {

            case SIGN_IN:

                // Not implemented. This module is currently the entry point. See onCreate.
                break;

            case FITNESS_INPUT:

                // SignInFragment -> MainActivity -> FitnessInputFragment

                mUser = new User(mBundle);
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

            case HEALTH:

                // FitnessInputFragment -> MainActivity -> BMRFragment (health module)

                mUser.updateFitnessData(mBundle);
                database.updateUser(mUser);

                /*
                 * The user's BMI and BMR data have yet to be added to the current User. This should occur
                 * when this module returns its Bundle.
                 */

                if (isTablet()) { // Sets up MasterView on the left for the remainder of the experience
                    Fragment masterListFragment = new MasterListFragment();
                    masterListFragment.setArguments(mBundle);
                    loadFragment(mFragmentManager, R.id.fl_fragment_placeholder_tablet_left,
                            masterListFragment, "TEST", false);
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

    /**
     * Returns the Fragment associated with the provided Module. In other words, pass a Module
     * and get back an instantiated Fragment representing that model.
     *
     * Important! The module being passed must exist in mMappedModules. It must also be a
     * Fragment. This is an important distinction for future extensions where a
     *
     *
     * ensure the module you are passing is a fragment, not an activity (extensibility)
     *
     * @param moduleToLoad
     * @return
     */
    @NonNull
    private Object getNextModuleView(Module moduleToLoad) {

        Object nextModuleView = null;
        try {
            Class<?> moduleClass = mMappedModules.get(moduleToLoad);
            Constructor<?> constructor = Class.forName(moduleClass.getName()).getConstructor();
            nextModuleView = constructor.newInstance();
        } catch (Exception e) {
            // This is a very useful log when adding additional modules.
            log("Failed to retrieve next loadable fragment: " + moduleToLoad.toString());
            e.printStackTrace();
        }
        return nextModuleView; // Don't let this be null... you'll crash in onDataPass.
    }
}

