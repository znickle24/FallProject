package com.zandernickle.fallproject_pt1;


import android.os.Bundle;
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


    public MasterListFragment() {
        // Required empty public constructor
    }

    // TODO: Move to ReusableUtil?
    private List<String> getModuleNames() {
        List<String> modules = new ArrayList<>();
        for (Module module : Module.values()) {
            modules.add(module.toString());
        }
        return modules;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisFragment = inflater.inflate(R.layout.fragment_master_list, container, false);

        mRecyclerView = thisFragment.findViewById(R.id.rv_master_list_fragment_placeholder);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        List<String> listItems = getModuleNames();

        mAdapter = new RVAdapter(listItems);
        mRecyclerView.setAdapter(mAdapter);

        return thisFragment;
    }

}
