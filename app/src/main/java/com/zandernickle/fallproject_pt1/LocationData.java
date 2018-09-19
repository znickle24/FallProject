package com.zandernickle.fallproject_pt1;

public class LocationData {

    //Variables holding location data
    private double mLatitude, mLongitude;
    private String mCity, mCountry;
    private long mSunrise, mSunset;

    //Getters and setters for latitude and longitude
    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    //Getters and setters for sunrise and sunset
    public long getSunrise() {
        return mSunrise;
    }

    public void setSunrise(long sunrise) {
        mSunrise = sunrise;
    }

    public long getSunset() {
        return mSunset;
    }

    public void setSunset(long sunset){
        mSunset = sunset;
    }

    //Getters and setters for city and country
    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

}
