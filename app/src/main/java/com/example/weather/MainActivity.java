package com.example.weather;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private TextView temperatureTextView;

    private ImageView weatherIconImageView;
    private Button getWeatherButton;

   // private WeatherData weatherData;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        getWeatherButton = findViewById(R.id.getWeatherButton);

        weatherIconImageView = findViewById(R.id.weatherIconImageView);

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString();
                if(city.trim().equals("")){
                    Toast.makeText(MainActivity.this, "Введите город", Toast.LENGTH_LONG).show();
                }
                else {
                    new GetWeatherTask().execute(city);
                }
            }
        });
    }

    private class GetWeatherTask extends AsyncTask<String, Void, WeatherData> implements com.example.weather.GetWeatherTask {

        @Override
        protected WeatherData doInBackground(String... params) {
            String city = params[0];
            String apiKey = "0ec53e1eb998c64df6b63a279e226a7a";

            try {
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    //return stringBuilder.toString();
                    return WeatherData.fromJson(stringBuilder.toString());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

       /* @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double celsiusTemperature = temperature - 273.15; // Конвертация из Кельвинов в Цельсии
                temperatureTextView.setText(String.format("%.2f°C", celsiusTemperature));
//////////
                int weatherIconResourceId = getResources().getIdentifier(weatherData.getWeatherIcon(), "drawable", getPackageName());
                if (weatherIconResourceId != 0) {
                    weatherIconImageView.setImageResource(weatherIconResourceId);
                } else {
                    // Если изображение не найдено, установите изображение по умолчанию
                    weatherIconImageView.setImageResource(R.drawable.default_weather_icon);
                }
                ////////////////
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
       @Override
       protected void onPostExecute(WeatherData weatherData) {
           super.onPostExecute(weatherData);

           if (weatherData != null) {
               double celsiusTemperature = weatherData.getTemperatureInCelsius();
               temperatureTextView.setText(String.format("%.2f°C", celsiusTemperature));

               // Загрузка изображения погоды по URL и установка его в ImageView
               new DownloadImageTask(weatherIconImageView).execute("https://openweathermap.org/img/wn/" + weatherData.getWeatherIcon() + "@2x.png");

              /* // Установка фона в зависимости от температуры
               if (celsiusTemperature < 10) {
                   findViewById(android.R.id.content).setBackgroundResource(R.drawable.cold_background);
               } else {
                   findViewById(android.R.id.content).setBackgroundResource(R.drawable.default_background);
               }*/
           } else {
               Toast.makeText(MainActivity.this, "Ошибка получения данных о погоде", Toast.LENGTH_LONG).show();
           }
       }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                // Если изображение не загружено, установите изображение по умолчанию
                imageView.setImageResource(R.drawable.default_weather_icon);
            }
        }
    }
}