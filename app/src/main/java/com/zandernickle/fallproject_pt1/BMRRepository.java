package com.zandernickle.fallproject_pt1;

import android.arch.lifecycle.MutableLiveData;

/**
 * Created by znickle on 10/10/18.
 */

public class BMRRepository {
    private final MutableLiveData<BMRData> bmrData = new MutableLiveData<BMRData>();
    private int BMR;
    private int BMI;

}
