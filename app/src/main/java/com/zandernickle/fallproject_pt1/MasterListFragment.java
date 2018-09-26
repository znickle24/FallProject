package com.zandernickle.fallproject_pt1;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class MasterListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public MasterListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisFragment = inflater.inflate(R.layout.fragment_master_list, container, false);

        mRecyclerView = thisFragment.findViewById(R.id.rv_master_list_fragment_placeholder);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        List<Module> listItems = ModuleUtil.getVisibleModuleList();

        mAdapter = new RVAdapter(getContext(), listItems);
        mRecyclerView.setAdapter(mAdapter);

        Bundle arguments = getArguments();
        Fragment masterListHeaderFragment = new MasterListHeaderFragment();
        masterListHeaderFragment.setArguments(arguments);

        ReusableUtil.loadFragment(getChildFragmentManager(), R.id.fl_master_list_header_placeholder,
                masterListHeaderFragment, "TEST", false);

        return thisFragment;
    }

}
