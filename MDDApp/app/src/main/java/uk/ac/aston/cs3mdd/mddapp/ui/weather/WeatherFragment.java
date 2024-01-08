package uk.ac.aston.cs3mdd.mddapp.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;

import org.w3c.dom.Text;

import uk.ac.aston.cs3mdd.mddapp.R;

public class WeatherFragment extends Fragment {

    private EditText cityEditText;
    private Button searchButton;
    private TextView textViewResult;
    private TextView variableTemperature;
    private TextView variableHumidity;
    private TextView variableWindSpeed;
    private TextView variableWeatherSummary;

    private WeatherViewModel weatherViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        cityEditText = rootView.findViewById(R.id.editTextCity);
        searchButton = rootView.findViewById(R.id.buttonSearch);
        variableTemperature = rootView.findViewById(R.id.variableTemperature);
        variableHumidity = rootView.findViewById(R.id.variableHumidity);
        variableWindSpeed = rootView.findViewById(R.id.variableWind);
        variableWeatherSummary = rootView.findViewById(R.id.variableWeatherSummary);
        textViewResult = rootView.findViewById(R.id.textViewResult);


        // Create ViewModel
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        // Observe LiveData
        weatherViewModel.getWeatherData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String weatherData) {

                // Parse the weather data to extract temperature and humidity
                String[] parts = weatherData.split("\n");
                if (parts.length == 3) {
                    String temperatureInfo = parts[0];
                    String humidityInfo = parts[1];
                    String windInfo = parts[2];

                    // Display temperature
                    variableTemperature.setText(temperatureInfo);

                    // Display humidity in variableHumidity
                    variableHumidity.setText(humidityInfo);

                    variableWindSpeed.setText(windInfo);

                }
            }
        });

        // Observe LiveData for weather summary
        weatherViewModel.getWeatherSummary().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String weatherSummary) {
                variableWeatherSummary.setText(weatherSummary);
            }
        });

        // Observe LiveData for error result
        weatherViewModel.getErrorResult().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorResult) {
                textViewResult.setText(errorResult);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityEditText.getText().toString().trim();
                if (!cityName.isEmpty()) {
                    // Clear the previous error message
                    textViewResult.setText("");
                    // Call ViewModel method to fetch weather data
                    weatherViewModel.fetchWeatherData(cityName, "84b224ebb3b42c9d995618bf8024f9bd");
                }
            }
        });

        return rootView;
    }
}
