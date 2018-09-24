package com.zandernickle.fallproject_pt1;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONWeather {

    /**
     * Parses JSON data to get weather data
     * @param data
     * @return WeatherData object that contains the current weather info
     * @throws JSONException
     */
    public static WeatherData getWeatherData(String data) throws JSONException{
        WeatherData weatherData = new WeatherData();

        //Parse JSON weather data
        JSONObject jsonObject = new JSONObject(data);

        WeatherData.CurrentCondition currentCondition = weatherData.getCurrentCondition();
        JSONObject jsonMain = jsonObject.getJSONObject("main");
        currentCondition.setHumidity(jsonMain.getInt("humidity"));
        currentCondition.setPressure(jsonMain.getInt("pressure"));
        weatherData.setCurrentCondition(currentCondition);

        //Get temp, wind, cloud, rain, and snow data
        WeatherData.Temperature temperature = weatherData.getTemperature();
        WeatherData.Wind wind = weatherData.getWind();
        WeatherData.Clouds clouds = weatherData.getClouds();
        WeatherData.Rain rain = weatherData.getRain();
        WeatherData.Snow snow = weatherData.getSnow();
        temperature.setMaxTemp(jsonMain.getDouble("temp_max"));
        temperature.setMinTemp(jsonMain.getDouble("temp_min"));
        temperature.setTemp(jsonMain.getDouble("temp"));
        weatherData.setTemperature(temperature);

        return weatherData;
    }
}
