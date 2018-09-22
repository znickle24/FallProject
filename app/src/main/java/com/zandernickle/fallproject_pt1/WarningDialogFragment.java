package com.zandernickle.fallproject_pt1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.neovisionaries.i18n.CountryCode;

import static com.zandernickle.fallproject_pt1.ReusableUtil.setOnClickListeners;

public class WarningDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextView mTvWarning;
    private Button mBtnNeg, mBtnPos;
    private OnWarningAcceptListener mWarningAcceptListener;

    public interface OnWarningAcceptListener {
        void onWarningAccepted();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mWarningAcceptListener = (OnWarningAcceptListener) getTargetFragment();
        } catch (ClassCastException e) {
            // TODO: Implement a more meaningful message.
            throw new ClassCastException();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View thisFragment = inflater.inflate(R.layout.dialog_weight_warning, container, false);

        mTvWarning = thisFragment.findViewById(R.id.tv_dialog_message);
        mBtnNeg = thisFragment.findViewById(R.id.button_neg);
        mBtnPos = thisFragment.findViewById(R.id.button_pos);

        setOnClickListeners(WarningDialogFragment.this, mBtnNeg, mBtnPos);

        // TODO: Set string here.
        return thisFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Passing a Bundle to this Dialog requires more memory than the CountryCode itself. However,
         * passing additional data is now much easier in the future.
         */
        Bundle arguments = getArguments();

        CountryCode countryCode = (CountryCode) arguments.getSerializable(Key.COUNTRY);
        int resourceId = countryCode == CountryCode.US ? R.string.unhealthy_weight_goal_warning_lbs :
                R.string.unhealthy_weight_goal_warning_kg;

        mTvWarning.setText(resourceId);

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.button_neg) {
            dismiss();
        } else if (viewId == R.id.button_pos) {
            dismiss(); // Must dismiss or this DialogFragment will appear atop the next loaded Fragment.
            mWarningAcceptListener.onWarningAccepted();
        }
    }
}
