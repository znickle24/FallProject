package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private Context mContext;
    private List<Module> mListItems; // Concatenated first and last names of each user
    private OnDataPass mDataPasser; // Pass to MainActivity

    public RVAdapter(Context context, List<Module> listItems) {
        mContext = context;
        mListItems = listItems;
    }

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

    private Drawable getModuleIcon(int position) {
        Module module = mListItems.get(position);
        int drawableId = -1;

        switch (module) {
            case HEALTH:
                drawableId = R.drawable.ic_health;
                break;
            case WEATHER:
                drawableId = R.drawable.ic_weather;
                break;
            case HIKES:
                drawableId = R.drawable.ic_hikes;
                break;
            default:
                break;
        }

        return mContext.getDrawable(drawableId);
    }

    @Override
    public void onBindViewHolder(@NonNull final RVAdapter.ViewHolder viewHolder, final int position) {

        Drawable icon = getModuleIcon(position);
        int selectedIconColor = mContext.getColor(R.color.colorAccent);

        // TODO: Change color on selection
//        icon.setTint();

        viewHolder.ivModuleIcon.setImageDrawable(icon);
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
        protected ImageView ivModuleIcon;

        public ViewHolder(View view) {
            super(view);
            itemLayout = view; // See item_layout.xml
            tvModuleName = view.findViewById(R.id.tv_module_name);
            ivModuleIcon = view.findViewById(R.id.iv_module_icon);
        }
    }
}
