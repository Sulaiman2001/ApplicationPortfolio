package uk.ac.aston.cs3mdd.mddapp.ui.weather;

// WeatherViewModel.java
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherViewModel extends ViewModel {
    private MutableLiveData<String> weatherData = new MutableLiveData<>();
    private MutableLiveData<String> weatherSummary = new MutableLiveData<>();
    private MutableLiveData<String> errorResult = new MutableLiveData<>();
    private WeatherApi weatherApi;
    public LiveData<String> getWeatherData() {
        return weatherData;
    }

    public LiveData<String> getWeatherSummary() {
        return weatherSummary;
    }

    public void fetchWeatherData(String cityName, String apiKey) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherApi = retrofit.create(WeatherApi.class);

        Call<WeatherResponse> call = weatherApi.getWeather(cityName, apiKey);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        // Process the response and update LiveData
                        updateWeatherData(weatherResponse);
                    }
                } else {
                    // Handle error
                    handleErrorResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                handleFailure();
            }
        });
    }

    private void updateWeatherData(WeatherResponse weatherResponse) {

        // Process the weatherResponse and update LiveData
        double temperatureKelvin = weatherResponse.getMain().getTemp();
        int temperatureCelsius = (int) (temperatureKelvin - 273.15);
        int humidity = weatherResponse.getMain().getHumidity();
        double windSpeed = weatherResponse.getWind().getSpeed();


        String temperatureString = temperatureCelsius + " ℃";
        String humidityString = humidity + "%";
        String windSpeedString = windSpeed + " m/s";

        // Set the LiveData with temperature and humidity separately
        weatherData.setValue(temperatureString + "\n" + humidityString + "\n" + windSpeedString);

        // Interpret weather conditions and update LiveData
        List<WeatherResponse.WeatherCondition> weatherConditions = weatherResponse.getWeatherConditions();
        String weatherSummaryValue = interpretWeatherConditions(weatherConditions);
        weatherSummary.setValue(weatherSummaryValue);
    }

    private String interpretWeatherConditions(List<WeatherResponse.WeatherCondition> weatherConditions) {
        if (weatherConditions != null && !weatherConditions.isEmpty()) {
            WeatherResponse.WeatherCondition firstCondition = weatherConditions.get(0);
            String mainCondition = firstCondition.getMain();
            String description = firstCondition.getDescription();

            return "Weather conditons: " + mainCondition + "\nWeather conditions detail: " + description;
        } else {
            return "Weather information not available";
        }
    }

    private void handleErrorResponse(int responseCode) {
        // Handle HTTP error responses
        if (responseCode == 404) {
            errorResult.setValue("City not found. Please enter a valid city name.");

        } else {
            errorResult.setValue("Cannot fetch data");

        }
    }

    private void handleFailure() {
        // Handle network failure
        weatherData.setValue("Network failure. Please try again later.");
    }

    public LiveData<String> getErrorResult() {
        return errorResult;
    }
}
