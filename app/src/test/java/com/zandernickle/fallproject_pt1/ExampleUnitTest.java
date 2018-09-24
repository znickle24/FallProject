package com.zandernickle.fallproject_pt1;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void getHeightAmerican_Test() throws Exception {
        //BMRFragment myClass = new WeatherData();
        String result1 = BMRFragment.getHeightAmerican(60); //enter number of inches
        String expected1 = "5'";
        //inches are "
        //feet are '
        assertEquals(result1, expected1);

        String result2 = BMRFragment.getHeightAmerican(66); //enter number of inches
        String expected2 = "5'6\"";
        assertEquals(result2, expected2);
    }
//
//
//    @Test
//    public void getHeightNonAmerican_Test() throws Exception {
//        //BMRFragment myClass = new WeatherData();
//        String result1 = BMRFragment.getHeightNonAmerican(168); //enter number of cm
//        String expected1 = "168cm.";
//        assertEquals(result1, expected1);
//    }
//
//
//    @Test
//    public void calculateBMI_Test() throws Exception {
//        //BMRFragment myClass = new WeatherData();
//        double result1 = BMRFragment.calculateBMI();
//        double expected1 = "5'";
//
//        assertEquals(result1, expected1);
//
//        double result2 = BMRFragment.calculateBMI();
//        double expected2 = "5'6\"";'
//        assertEquals(result2, expected2);
//    }
//
//
//    @Test
//    public void determineBMICategory_Test() throws Exception {
//        String result1 = BMRFragment.determineBMICategory(16.5); //enter BMI number
//        String expected1 = "Underweight";
//        assertEquals(result1, expected1);
//
//        String result2 = BMRFragment.determineBMICategory(23.7); //enter BMI number
//        String expected2 = "Normal Weight";
//        assertEquals(result2, expected2);
//
//        String result3 = BMRFragment.determineBMICategory(25.2); //enter BMI number
//        String expected3 = "Overweight";
//        assertEquals(result3, expected3);
//
//        String result4 = BMRFragment.determineBMICategory(30); //enter BMI number
//        String expected4 = "Obese";
//        assertEquals(result4, expected4);
//    }


}