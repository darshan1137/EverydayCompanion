package com.example.everydaycompanion;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherActivity extends AppCompatActivity {

    private EditText cityInput;
    private TextView weatherResult;
    private final String API_KEY = "ac1644d61cd6dfc3233cbff1b91eed58"; // Replace with your OpenWeatherMap API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityInput = findViewById(R.id.cityInput);
        weatherResult = findViewById(R.id.weatherResult);
        Button getWeatherBtn = findViewById(R.id.getWeatherBtn);

        getWeatherBtn.setOnClickListener(v -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                new GetWeatherTask().execute(city);
            } else {
                Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GetWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... cities) {
            try {
                String city = URLEncoder.encode(cities[0], "UTF-8");
                String urlStr = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                        "&appid=" + API_KEY + "&units=metric";
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String city = jsonObject.getString("name");
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temp = main.getDouble("temp");
                    double feelsLike = main.getDouble("feels_like");

                    JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
                    String description = weather.getString("description");

                    String weatherText = "City: " + city +
                            "\nTemperature: " + temp + "°C" +
                            "\nFeels like: " + feelsLike + "°C" +
                            "\nCondition: " + description;

                    weatherResult.setText(weatherText);
                } catch (Exception e) {
                    weatherResult.setText("Error parsing weather data.");
                }
            } else {
                weatherResult.setText("Failed to fetch weather. Please try again.");
            }
        }
    }
}
