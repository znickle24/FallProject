package com.zandernickle.fallproject_pt1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MasterListHeaderFragment extends Fragment {

    TextView mTvUserName;

    public MasterListHeaderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragment = inflater.inflate(R.layout.fragment_master_list_header, container, false);

        mTvUserName = thisFragment.findViewById(R.id.tv_user_name);
        mTvUserName.setText(getArguments().getString(Key.NAME));

        return thisFragment;
    }

}
