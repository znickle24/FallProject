package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<Module> mListItems; // Concatenated first and last names of each user
    private OnDataPass mDataPasser; // Pass to MainActivity

    public RVAdapter(List<Module> listItems) { mListItems = listItems; }

    @NonNull
    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();

        try {
            mDataPasser = (OnDataPass) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataPass");
        }

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RVAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.tvModuleName.setText(mListItems.get(position).toString());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Module nextModuleToLoad = mListItems.get(position);
                mDataPasser.onDataPass(nextModuleToLoad, null);
                
            }
        };

        viewHolder.itemLayout.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public interface OnDataPass {
        void onDataPass(Module moduleToLoad, Bundle bundle);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected View itemLayout;
        protected TextView tvModuleName;

        public ViewHolder(View view) {
            super(view);
            itemLayout = view; // See item_layout.xml
            tvModuleName = view.findViewById(R.id.tv_module_name);
        }
    }
}
