package com.zandernickle.fallproject_pt1;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Network {
    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String APPIDQUERY = "&appid=";
    private static final String app_id = "2f0c2eee1a7802a82ad45d5849e59d6a"; //OpenWeather API Key

    /**
     *
     * @param location
     * @return
     */
    public static URL buildURLFromString(String location){
        URL myURL = null;
        try {
            myURL = new URL(BASE_URL + location + APPIDQUERY + app_id);
            Log.d("myURL in buildURLFromString: ", myURL.toString());
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
        return myURL;
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String getDataFromURL(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            //Use scanner trick - search for the next "beginning" of the input stream
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            } else {
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }

}
