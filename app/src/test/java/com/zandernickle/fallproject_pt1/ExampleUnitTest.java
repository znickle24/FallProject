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
        int mWeight = 200;
        int mInches = 72;

        //test American BMI calculation
        double bmiResult1 = Math.round(((mWeight)/Math.pow(mInches, 2)) * 703 * 10.0)/ 10.0;
        double bmiExpected1 = 27.1;
        assertEquals(Double.toString(bmiResult1), Double.toString(bmiExpected1));
    }

    //BMRFragment.java
    @Test
    public void calculateBMR_Test() throws Exception {
        int mWeight = 200;
        int mInches = 72;
        int mAge = 25;

        //Male BMR
        double BMRResult1 = 66 + (6.23 * mWeight) + (12.7 * mInches) - (6.8 * mAge);
        double BMRExpected1 = 2056.4;
        assertEquals(Double.toString(BMRResult1), Double.toString(BMRExpected1));

        //Female BMR
        double BMRResult2 = 655 + (4.35 * mWeight) + (4.7 * mInches) - (4.7 * mAge);
        double BMRExpected2 = 1745.9;
        assertEquals(Double.toString(BMRResult2), Double.toString(BMRExpected2));
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