package com.zandernickle.fallproject_pt1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.zandernickle.fallproject_pt1.ReusableUtil.loadMenuBarFragment;


public class PlaygroundFragment extends Fragment {

    public PlaygroundFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_playground, container, false);

        Bundle arguments = getArguments();

        // Extract data needed for this module fragment

        // Pass the module name and the profile image on to the menu bar fragment
        // This method is in the ReusableUtil class.
        loadMenuBarFragment(PlaygroundFragment.this, arguments, R.id.fl_menu_bar_fragment_placeholder);

        return thisFragment;
    }


}
