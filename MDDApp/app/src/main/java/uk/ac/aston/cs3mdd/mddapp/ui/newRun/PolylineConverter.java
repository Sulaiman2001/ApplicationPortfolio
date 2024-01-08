package uk.ac.aston.cs3mdd.mddapp.ui.newRun;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class PolylineConverter {

    // Uses longitude and latitude
    public static String convertPolylineToString(List<LatLng> polyline) {
        StringBuilder stringBuilder = new StringBuilder();

        for (LatLng point : polyline) {
            Log.d("PolylineConverter", "Latitude: " + point.latitude + ", Longitude: " + point.longitude);

            stringBuilder.append(point.latitude)
                    .append(",")
                    .append(point.longitude)
                    .append(";");
        }

        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        String polylineString = stringBuilder.toString();

        Log.d("PolylineConverter", "Polyline String: " + polylineString);

        return stringBuilder.toString();
    }

    public static List<LatLng> parsePolyline(String polylineString) {
        List<LatLng> polyline = new ArrayList<>();

        if (polylineString != null && !polylineString.isEmpty()) {
            String[] points = polylineString.split(";");

            for (String point : points) {
                String[] coordinates = point.split(",");
                if (coordinates.length == 2) {
                    double latitude = Double.parseDouble(coordinates[0]);
                    double longitude = Double.parseDouble(coordinates[1]);
                    polyline.add(new LatLng(latitude, longitude));
                }
            }
        }

        return polyline;
    }
}
