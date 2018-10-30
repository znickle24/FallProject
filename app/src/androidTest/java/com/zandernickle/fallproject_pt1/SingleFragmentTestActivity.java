package com.zandernickle.fallproject_pt1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * A container for testing fragments in isolation with Espresso.
 *
 * @see <a href="https://medium.com/@aitorvs/isolate-your-fragments-just-for-testing-ea7d4fddcba2">http://medium.com</a>
 */
public class SingleFragmentTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout content = new FrameLayout(this);
        content.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        content.setId(R.id.content_frame);
        setContentView(content);
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, fragment, "TEST")
                .commit();
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
    }
}
