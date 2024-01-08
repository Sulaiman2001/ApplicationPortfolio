package uk.ac.aston.cs3mdd.mddapp.ui.weather;

// WeatherApi.java
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherResponse> getWeather(@Query("q") String cityName, @Query("appid") String apiKey);
}
