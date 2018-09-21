package com.zandernickle.fallproject_pt1;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class CustomSeekBar extends android.support.v7.widget.AppCompatSeekBar {


    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBounds(int min, int max) {
        setMin(min);
        setMax(max);
    }
}
