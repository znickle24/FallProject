package com.zandernickle.fallproject_pt1;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.zandernickle.fallproject_pt1.ReusableUtil.setOnClickListeners;

/**
 * A MenuBarFragment which does not contain the menu icon at the top left. For tablets, the master list
 * will be visible at all times. Therefore, there is no need for a menu icon.
 */
public class MenuBarTabletFragment extends MenuBarFragment {


    public MenuBarTabletFragment() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     *
     * All we did here was drop the mTvMenuIcon element since we don't need it for tablets.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisFragment = inflater.inflate(R.layout.fragment_menu_bar_tablet, container, false);

        mTvModuleName = thisFragment.findViewById(R.id.tv_module_name);
        mIvProfileImage = thisFragment.findViewById(R.id.civ_profile_image);

        setOnClickListeners(MenuBarTabletFragment.this, mIvProfileImage);

        return thisFragment;
    }

}
