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
        FitnessInputFragment.OnDataPass, MenuBarFragment.OnDataPass {

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
     *
     *
     */

    private DatabaseService database = new DatabaseService(); // A placeholder for a real database.
    private FragmentManager mFragmentManager = getSupportFragmentManager(); // Reusable throughout the application.
    private User mUser; // The current user (kind of like a Cookie).

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

        int viewId = isTablet() ? R.id.fl_fragment_placeholder_tablet_right :
                R.id.fl_fragment_placeholder_phone;

        loadFragment(mFragmentManager, viewId, new SignInFragment(), SIGN_IN_FRAGMENT, false);
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
    public void onDataPass(String key, Bundle bundle) {

        // TODO: Decide how often to update the database.

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

                loadFragment(mFragmentManager, signInFragment.getId(), fitnessInputFragment, FITNESS_INPUT_FRAGMENT, false);

                break;

            case FITNESS_INPUT_FRAGMENT:

                mUser.updateFitnessData(bundle);
                database.updateUser(mUser);

                /*
                 * At this point the user's profile has been updated with their basic fitness information (see the
                 * User.updateFitnessData method). However, their BMI and BMR have yet to be calculated. These are
                 * added when the BMR fragment returns its data.
                 */

//                mWeight = mArgsReceived.getInt(Key.WEIGHT);
//                mAge = mArgsReceived.getInt(Key.AGE);
//                mInches = mArgsReceived.getInt(Key.HEIGHT);
//                mWeightGoal = mArgsReceived.getInt(Key.GOAL);


                // TESTS ........

                Bundle testBundle = new Bundle();
                testBundle.putSerializable(Key.MODULE, Module.HEALTH);
                testBundle.putByteArray(Key.PROFILE_IMAGE, mUser.getProfileImage());

                Fragment prevFitnessInputFragment = mFragmentManager.findFragmentByTag(FITNESS_INPUT_FRAGMENT);
                Fragment playgroundFragment = new PlaygroundFragment();
                playgroundFragment.setArguments(testBundle);

                loadFragment(mFragmentManager, prevFitnessInputFragment.getId(), playgroundFragment, "TEST", false);

                // End tests ........

                break;

            case MENU_BAR_FRAGMENT_MENU_PRESSED: // Ignore the Bundle, its null
                ReusableUtil.toast(this, "Load the RecyclerView");
                break;
            case MENU_BAR_FRAGMENT_PROFILE_PRESSED: // Ignore the Bundle, its null
                ReusableUtil.toast(this, "Load the UpdateProfileFragment");
                break;
        }
    }

}

