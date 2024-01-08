package uk.ac.aston.cs3mdd.mddapp.ui.newRun;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.cs3mdd.mddapp.R;
public class LocationTrackingService extends Service {

    private static final String CHANNEL_ID = "LocationTrackingChannel";
    private static final int NOTIFICATION_ID = 123;

    public static final String ACTION_LOCATION_UPDATE = "uk.ac.aston.cs3mdd.mddapp.ACTION_LOCATION_UPDATE";
    public static final String EXTRA_LOCATION = "extra_location";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private List<LatLng> routePoints = new ArrayList<>();
    private PolylineOptions polylineOptions;
    private Polyline routePolyline;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        polylineOptions = new PolylineOptions().color(ContextCompat.getColor(this, R.color.orange)).width(10);
        startLocationUpdates();
    }

    // Create notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Tracking Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // Create the foreground notification
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Tracking")
                .setContentText("Tracking your location...")
                .setSmallIcon(R.drawable.ic_add);

        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(1000) // Update interval in milliseconds
                .setFastestInterval(500) // Fastest update interval in milliseconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        onNewLocationReceived(location);
                    }
                }
            }
        };

        if (checkLocationPermission()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private boolean checkLocationPermission() {
        return true; // For simplicity, assuming permission is always granted
    }

    private void onNewLocationReceived(Location location) {
        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        routePoints.add(newLatLng);

        // Broadcast location  update to the NewRunFragment
        Intent intent = new Intent(ACTION_LOCATION_UPDATE);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        float distance = calculateDistance(newLatLng);

        // Update the polyline in the background
        if (routePolyline != null) {
            routePolyline.setPoints(routePoints);
        }
    }

    private float calculateDistance(LatLng newLatLng) {
        if (routePoints.size() > 1) {
            LatLng lastLatLng = routePoints.get(routePoints.size() - 2);
            float[] results = new float[1];
            Location.distanceBetween(lastLatLng.latitude, lastLatLng.longitude, newLatLng.latitude, newLatLng.longitude, results);
            return results[0];
        }
        return 0;
    }
}