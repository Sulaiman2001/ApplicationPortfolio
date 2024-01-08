// WeatherResponse.java
package uk.ac.aston.cs3mdd.mddapp.ui.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {
    @SerializedName("main")
    private Main main;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("weather")
    private List<WeatherCondition> weatherConditions;

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public List<WeatherCondition> getWeatherConditions() {
        return weatherConditions;
    }

    public class Main {
        @SerializedName("temp")
        private double temp;

        @SerializedName("humidity")
        private int humidity;

        public double getTemp() {
            return temp;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    public class Wind {
        @SerializedName("speed")
        private double speed;

        public double getSpeed() {
            return speed;
        }
    }

    public class WeatherCondition {
        @SerializedName("id")
        private int id;

        @SerializedName("main")
        private String main;

        @SerializedName("description")
        private String description;

        public int getId() {
            return id;
        }

        public String getMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }
    }
}
