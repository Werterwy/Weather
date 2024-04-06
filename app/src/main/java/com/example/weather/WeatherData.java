package com.example.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {
    private double temperatureInCelsius;
    private String weatherIcon;

    public static WeatherData fromJson(String jsonString) throws JSONException {
        WeatherData weatherData = new WeatherData();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject main = jsonObject.getJSONObject("main");
        weatherData.setTemperatureInCelsius(main.getDouble("temp") - 273.15);

        JSONArray weatherArray = jsonObject.getJSONArray("weather");
        if (weatherArray.length() > 0) {
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            weatherData.setWeatherIcon(weatherObject.getString("icon"));
        }

        return weatherData;
    }

    public double getTemperatureInCelsius() {
        return temperatureInCelsius;
    }

    public void setTemperatureInCelsius(double temperatureInCelsius) {
        this.temperatureInCelsius = temperatureInCelsius;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }
}
