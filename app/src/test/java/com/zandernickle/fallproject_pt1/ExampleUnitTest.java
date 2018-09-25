package com.zandernickle.fallproject_pt1;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    //BMRFragment.java
    //inches are "; feet are '
    @Test
    public void getHeightAmerican_Test() throws Exception {
        String result1 = BMRFragment.getHeightAmerican(60); //# of inches
        String expected1 = "5'";
        assertEquals(result1, expected1);

        String result2 = BMRFragment.getHeightAmerican(66); //# of inches
        String expected2 = "5'6\"";
        assertEquals(result2, expected2);
    }

    //BMRFragment.java
    @Test
    public void getHeightNonAmerican_Test() throws Exception {
        String result1 = BMRFragment.getHeightNonAmerican(168); //# of cm
        String expected1 = "168cm.";
        assertEquals(result1, expected1);
    }

    //BMRFragment.java
    @Test
    public void calculateBMI_Test() throws Exception {
        BMRFragment bmr = new BMRFragment();
        //boolean bmr.mAmerican = true;
        int mWeight = 200;
        int mInches = 72;

        double result1 = BMRFragment.calculateBMI();
        double expected1 = 27.1;
        assertEquals(Double.toString(result1), Double.toString(expected1));


        //mAmerican = false;


//    if (mAmerican) {
//        bmi = (mWeight * 703)/mInches;
//    } else {
//        //need to take the cm out of mHeight then should work
//        double numberOfMeters = 0.0;
//        double additionalCM = 0.0;
//        if (mInches > 100) {
//            if (mInches > 200) {
//                //unlikely, but plenty of people surpass this
//                numberOfMeters = 2;
//                additionalCM = mInches - 200;
//            } else {
//                //regular case
//                numberOfMeters = 1;
//                additionalCM = mInches - 100;
//            }
//        } else {
//            numberOfMeters = 0;
//        }
//        additionalCM = additionalCM/100.0;
//        double heightInMeters = numberOfMeters + additionalCM;
//        bmi = mWeight / Math.pow(heightInMeters, 2);


//        //BMRFragment myClass = new WeatherData();

//
//        assertEquals(result1, expected1);
//
//        double result2 = BMRFragment.calculateBMI();
//        double expected2 = "5'6\"";'
//        assertEquals(result2, expected2);
//    }
    }

    //BMRFragment.java
    @Test
    public void determineBMICategory_Test() throws Exception {
        String result1 = BMRFragment.determineBMICategory(16.5); //enter BMI number
        String expected1 = "Underweight";
        assertEquals(result1, expected1);

        String result2 = BMRFragment.determineBMICategory(23.7); //enter BMI number
        String expected2 = "Normal Weight";
        assertEquals(result2, expected2);

        String result3 = BMRFragment.determineBMICategory(25.2); //enter BMI number
        String expected3 = "Overweight";
        assertEquals(result3, expected3);

        String result4 = BMRFragment.determineBMICategory(30); //enter BMI number
        String expected4 = "Obese";
        assertEquals(result4, expected4);
    }


}