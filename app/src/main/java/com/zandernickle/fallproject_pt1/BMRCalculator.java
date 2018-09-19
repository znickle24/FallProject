package com.zandernickle.fallproject_pt1;

import android.os.Bundle;

/**
 * Created by znickle on 9/18/18.
 */

public class BMRCalculator {
    private double BMR;
    private double weight = 0.0;
    private String weightVal;
    private int inch = 12;
    private int inches;
    private int age;
    public double getWeight() {
        weight = Double.parseDouble(Key.WEIGHT);
        return weight;
    }

    //formulas differ depending on whether you're a male or female
    if (Sex.MALE == "") {
        BMR = 66 + (6.23 * weight) + (12.7 * inches) - (6.8 * age);
    } else { //is female
        BMR = 655 + ( 4.35 * weight ) + ( 4.7 * inches) - ( 4.7 * age);
    }
    //for BMRFragment to be accurate, you need to multiply it by your activity level
    //may want to add different levels of activity
    if (ActivityLevel.ACTIVE != null) {
        BMR *= 1.725;
    } else {
        BMR *= 1.2;
    }
    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        savedInstanceState.putDouble("BMRFragment", BMR);
        savedInstanceState.putDouble(Key.WEIGHT, weight);
        savedInstanceState.putInt("INCHES", inches);
        savedInstanceState.putInt("AGE", age);
        super.onSaveInstanceState(savedInstanceState);
    }
}
