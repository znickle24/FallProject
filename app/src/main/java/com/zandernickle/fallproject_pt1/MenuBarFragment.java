package com.zandernickle.fallproject_pt1;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static com.zandernickle.fallproject_pt1.Key.MENU_BAR_FRAGMENT_MENU_PRESSED;
import static com.zandernickle.fallproject_pt1.Key.MENU_BAR_FRAGMENT_PROFILE_PRESSED;
import static com.zandernickle.fallproject_pt1.ReusableUtil.bitmapFromBundle;
import static com.zandernickle.fallproject_pt1.ReusableUtil.setOnClickListeners;


public class MenuBarFragment extends Fragment implements View.OnClickListener {

    protected TextView mTvModuleName;
    protected ImageView mIvMenuIcon, mIvProfileImage;

    protected Bitmap mBitmapProfileImage;

    protected OnDataPass mDataPasser;


    public MenuBarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // Profile image
    }

    /**
     * {@inheritDoc}
     *
     * Attaches this Fragment's OnDataPass interface to the host Activity (Main). This bypasses the fragment that
     * created this fragment (some module)
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mDataPasser = (MenuBarFragment.OnDataPass) context;
        } catch (ClassCastException e) {
            // This should never happen; if it does, its nice to have a decent error message.
            String message = context.toString() + " must implement " + this.toString() + ".OnDataPass";
            throw new ClassCastException(message);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thisFragment = inflater.inflate(R.layout.fragment_menu_bar, container, false);

        mIvMenuIcon = thisFragment.findViewById(R.id.iv_menu_icon);
        mTvModuleName = thisFragment.findViewById(R.id.tv_module_name);
        mIvProfileImage = thisFragment.findViewById(R.id.civ_profile_image);

        setOnClickListeners(MenuBarFragment.this, mIvMenuIcon, mIvProfileImage);

        return thisFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();

        Module moduleName = (Module) arguments.getSerializable(Key.MODULE);
        User activeUser = arguments.getParcelable(Key.USER);
        mBitmapProfileImage = ReusableUtil.byteArrayToBitmap(activeUser.getProfileImage());

        mTvModuleName.setText(moduleName.toString());
        mIvProfileImage.setImageBitmap(mBitmapProfileImage);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu_icon:
                mDataPasser.onDataPass(Module.MASTER_LIST, null);
                break;
            case R.id.civ_profile_image:
                mDataPasser.onDataPass(Module.SIGN_IN, null);
                break;
        }
    }

    public interface OnDataPass {
        void onDataPass(Module moduleToLoad, @Nullable Bundle bundle);
    }
}
