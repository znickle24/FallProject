package com.zandernickle.fallproject_pt1;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import static com.zandernickle.fallproject_pt1.Key.SIGN_IN_FRAGMENT;
import static com.zandernickle.fallproject_pt1.ReusableUtil.loadFragment;
import static com.zandernickle.fallproject_pt1.ReusableUtil.log;

/**
 * This application's entry point. Use this class to pass data from one Fragment (or Activity) to the next. Register
 * additional modules in ModuleUtil.
 */
public class MainActivity extends CustomAppCompatActivity implements SignInFragment.OnDataPass,
        FitnessInputFragment.OnDataPass, MenuBarFragment.OnDataPass, RVAdapter.OnDataPass, BMRFragment.OnDataPass {

    public static final String PREV_FRAGMENT_TAG = "PREV_FRAGMENT_TAG";
    private static final HashMap<Module, Class<?>> mMappedModules = ModuleUtil.mapModuleList();

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private String mPrevFragmentTag;
    private Bundle mBundle;

    private User mUser;
    private UserRepository mUserRepo;

    private void loadModule() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserRepo = new UserRepository(MainActivity.this.getApplication()); // instantiate here important!

        boolean isTablet = isTablet();
        int viewId = isTablet ? R.id.fl_fragment_placeholder_tablet_right :
                R.id.fl_fragment_placeholder_phone;

        if (savedInstanceState == null) {

            mBundle = new Bundle();
            mBundle.putBoolean(Key.IS_TABLET, isTablet);

            int activeUserId = mUserRepo.getActiveUserId();
            if (activeUserId != UserRepository.NON_EXISTENT_ID) {
                mUser = mUserRepo.getUserSync(activeUserId);
                mBundle.putSerializable(Key.MODULE, Module.HEALTH);
                mBundle.putParcelable(Key.USER, mUser);

                if (isTablet()) { // Sets up MasterView on the left for the remainder of the experience
                    Fragment masterListFragment = new MasterListFragment();
                    masterListFragment.setArguments(mBundle);
                    loadFragment(mFragmentManager, R.id.fl_fragment_placeholder_tablet_left,
                            masterListFragment, "TEST", false);
                }

                mPrevFragmentTag = Module.HEALTH.toString();
                Fragment bmrFragment = new BMRFragment();
                bmrFragment.setArguments(mBundle);
                loadFragment(mFragmentManager, viewId, bmrFragment, mPrevFragmentTag, false);

            } else {

                mPrevFragmentTag = SIGN_IN_FRAGMENT; // TODO: change this name
                Fragment signInFragment = new SignInFragment();
                signInFragment.setArguments(mBundle);
                loadFragment(mFragmentManager, viewId, signInFragment, mPrevFragmentTag, false);
            }

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
                log("FINISHED SIGN-IN... UPDATING DATABASE");
                int id = mUserRepo.addUser(mUser);
                mUser.setId(id);
                mUserRepo.updateActiveUser(id);

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

                log("FINISHED FITNESS INPUT... UPDATING DATABASE");

                /* TODO
                 *
                 * This is a temporary line of code corresponding with the temporary code inside the
                 * SignIn and FitnessInput Fragments. See Reusable.setPortraitOnly.
                 *
                 * Only the first two sign in screens have their orientation locked (sign in data). All
                 * others will be unlocked here.
                 */
                MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                // FitnessInputFragment -> MainActivity -> BMRFragment (health module)

                mUser.updateFitnessData(mBundle);
                // TODO: Update fitness data here.

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

