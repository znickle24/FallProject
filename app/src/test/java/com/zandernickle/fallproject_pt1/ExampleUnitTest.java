package com.zandernickle.fallproject_pt1;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Local unit tests, which will execute on the development machine (host).
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

        String result2 = BMRFragment.getHeightNonAmerican(0); //# of cm
        String expected2 = "0cm.";
        assertEquals(result2, expected2);
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

        int mWeight2 = 120;
        int mInches2 = 65;

        //test American BMI calculation
        double bmiResult2 = Math.round(((mWeight2)/Math.pow(mInches2, 2)) * 703 * 10.0)/ 10.0;
        double bmiExpected2 = 20.0;
        assertEquals(Double.toString(bmiResult2), Double.toString(bmiExpected2));
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
        String result1 = BMRFragment.determineBMICategory(16.5); //BMI #
        String expected1 = "Underweight";
        assertEquals(result1, expected1);

        String result2 = BMRFragment.determineBMICategory(23.7); //BMI #
        String expected2 = "Normal Weight";
        assertEquals(result2, expected2);

        String result3 = BMRFragment.determineBMICategory(25.2); //BMI #
        String expected3 = "Overweight";
        assertEquals(result3, expected3);

        String result4 = BMRFragment.determineBMICategory(30); //BMI #
        String expected4 = "Obese";
        assertEquals(result4, expected4);
    }

    //UnitConversionUtil.java
    @Test
    public void insToCms_Test() throws Exception {
        int result1 = UnitConversionUtil.insToCms(72); //# of inches
        int expected1 = 182;
        assertEquals(result1, expected1);

        int result2 = UnitConversionUtil.insToCms(66); //# of inches
        int expected2 = 167;
        assertEquals(result2, expected2);

        int result3 = UnitConversionUtil.insToCms(0); //# of inches
        int expected3 = 0;
        assertEquals(result3, expected3);
    }

    //UnitConversionUtil.java
    @Test
    public void lbsToKgs_Test() throws Exception {
        int result1 = UnitConversionUtil.lbsToKgs(200); //# of lbs
        int expected1 = 90;
        assertEquals(result1, expected1);

        int result2 = UnitConversionUtil.lbsToKgs(120); //# of lbs
        int expected2 = 54;
        assertEquals(result2, expected2);

        int result3 = UnitConversionUtil.lbsToKgs(0); //# of lbs
        int expected3 = 0;
        assertEquals(result3, expected3);
    }

}