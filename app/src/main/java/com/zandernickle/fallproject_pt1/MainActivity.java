package com.zandernickle.fallproject_pt1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import static com.zandernickle.fallproject_pt1.ReusableUtil.loadFragment;

public class MainActivity extends CustomAppCompatActivity implements SignInFragment.OnDataPass, FitnessInputFragment.OnDataPass {

    // TODO: Put these in Key.java FOR USE IN ONDATAPASS
    private static final String SIGN_IN_FRAGMENT = "SIGN_IN_FRAGMENT";
    private static final String FITNESS_INPUT_FRAGMENT = "FITNESS_INPUT_FRAGMENT";

    private DatabaseServiceUtil database = new DatabaseServiceUtil();
    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int viewId = isTablet() ? R.id.fl_fragment_placeholder_tablet_right :
                R.id.fl_fragment_placeholder_phone;

        loadFragment(mFragmentManager, viewId, new SignInFragment(), SIGN_IN_FRAGMENT, false);
    }

    @Override
    public void onDataPass(String key, Bundle signInBundle) {

        switch (key) {
            case SIGN_IN_FRAGMENT:
                user = new User(signInBundle);
                database.addUser(user); // TODO: Add user here or after the next fragment returns?

                Fragment signInFragment = mFragmentManager.findFragmentByTag(SIGN_IN_FRAGMENT);
                Fragment fitnessInputFragment = new FitnessInputFragment();
                fitnessInputFragment.setArguments(signInBundle);

                loadFragment(mFragmentManager, signInFragment.getId(), fitnessInputFragment,
                        FITNESS_INPUT_FRAGMENT, true);

                break;
            case FITNESS_INPUT_FRAGMENT:

//                // TODO: Store as member variable instead of looking up from tag or just use scope of switch?
//                Fragment returnedFitnessInputFragment = mFragmentManager.findFragmentByTag(FITNESS_INPUT_FRAGMENT);
//                loadFragment(mFragmentManager, returnedFitnessInputFragment.getId(), new BMRFragment(), "TEST", false);
                break;
        }
    }

}

