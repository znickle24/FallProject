package com.zandernickle.fallproject_pt1;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class TestSignInFragment {

    @Rule
    public ActivityTestRule<SingleFragmentTestActivity> activityTestRule =
            new ActivityTestRule<>(SingleFragmentTestActivity.class);

    @Before
    public void init(){
        activityTestRule.getActivity().setFragment(new SignInFragment());
    }

    @Test
    public void TestViewVisibility() {
        onView(withId(R.id.tv_sign_up)).check(matches((isDisplayed())));
    }

}

