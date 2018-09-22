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
    public void getHeightAmerican_isCorrect() throws Exception {
        //BMRFragment myClass = new WeatherData();
        String result1 = BMRFragment.getHeightAmerican(60); //enter number of inches
        String expected1 = "5'";
        //inches are "
        //feet are '
        assertEquals(result1, expected1);

        String result2 = BMRFragment.getHeightAmerican(66); //enter number of inches
        String expected2 = "5'6\"";'
        assertEquals(result2, expected2);
    }


    @Test
    public void getHeightAmerican_isCorrect() throws Exception {
        //BMRFragment myClass = new WeatherData();
        String result1 = BMRFragment.getHeightAmerican(60); //enter number of inches
        String expected1 = "5'";
        //inches are "
        //feet are '
        assertEquals(result1, expected1);

        String result2 = BMRFragment.getHeightAmerican(66); //enter number of inches
        String expected2 = "5'6\"";'
        assertEquals(result2, expected2);
    }


}