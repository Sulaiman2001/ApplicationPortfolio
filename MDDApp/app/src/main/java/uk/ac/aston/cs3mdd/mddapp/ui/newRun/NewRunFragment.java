package uk.ac.aston.cs3mdd.mddapp.ui.newRun;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.ac.aston.cs3mdd.mddapp.R;

public class NewRunFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean isRunning = false;
    private long startTimeMillis;
    private List<LatLng> routePoints = new ArrayList<>();
    private Polyline routePolyline;
    private PolylineOptions polylineOptions;
    private FloatingActionButton startButton;
    private FloatingActionButton stopButton;
    private TextView stopwatch;
    private TextView distanceCovered;
    private float totalDistance = 0;
    private RunRepository runRepository;
    private LatLng startLatLng;
    private TextView averagePace;
    private boolean running = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_run, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (!isServiceRunning(LocationTrackingService.class)) {
            startLocationService();
        }

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        startButton = rootView.findViewById(R.id.startButton);
        stopButton = rootView.findViewById(R.id.stopButton);
        stopwatch = rootView.findViewById(R.id.stopwatch);
        averagePace = rootView.findViewById(R.id.averagePace);
        distanceCovered = rootView.findViewById(R.id.distanceCovered);
        runRepository = new RunRepository(requireActivity().getApplication());

        // Set up click listener for startButton
        if (startButton != null) {
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRunStart();
                }
            });
        }

        // Set up click listener for stopButton
        if (stopButton != null) {
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRunStop();
                }
            });
        }

        return rootView;
    }



    private BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(LocationTrackingService.EXTRA_LOCATION)) {
                Location location = intent.getParcelableExtra(LocationTrackingService.EXTRA_LOCATION);
                onNewLocationReceived(location);
            }
        }
    };

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();

        startButton = requireView().findViewById(R.id.startButton);
        stopButton = requireView().findViewById(R.id.stopButton);

        // Initialize PolylineOptions
        polylineOptions = new PolylineOptions()
                .color(ContextCompat.getColor(requireContext(), R.color.orange))
                .width(10); // You can adjust the color and width as needed

        drawRoutePolyline();

    }

    private void drawRoutePolyline() {
        // Initialize or update the polyline
        if (routePolyline == null) {
            // Create a new polyline if it doesn't exist
            routePolyline = map.addPolyline(polylineOptions);
        } else {
            // Clear the existing points if the polyline already exists
            routePolyline.setPoints(new ArrayList<>());
        }

        routePolyline.setVisible(true);
        // Add the updated points to the polyline
        routePolyline.setPoints(routePoints);
    }

    // Enable location on the map without starting the service
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Location permission granted, enable location on the map
            map.setMyLocationEnabled(true);

            // Get the last known location only if the location permission is granted
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));

                                // Call onNewLocationReceived with the last known location
                                onNewLocationReceived(location);
                            }
                        }
                    });

            // Request location updates
            fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());
        } else {
            // Location permission not granted, request it
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private LocationRequest createLocationRequest() {
        return new LocationRequest()
                .setInterval(1000) // Update interval in milliseconds
                .setFastestInterval(500) // Fastest update interval in milliseconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null) {
                for (Location location : locationResult.getLocations()) {
                    onNewLocationReceived(location);
                }
            }
        }
    };

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(requireContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(requireContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if the permission request is for location
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        // Check if location permission is granted
        if (isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION) ||
                isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Permission granted, perform location-related actions
            enableMyLocation();  // Enable location on the map
            startLocationService();  // You can move this line here if needed

            // Set up "Start" button click listener (if needed)
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRunStart();  // Continue with the start action
                }
            });
        } else {
            // Permission denied, show an error message
            permissionDenied = true;
            showMissingPermissionError();
        }
    }


    private boolean isPermissionGranted(String[] permissions, int[] grantResults, String permission) {
        for (int i = 0; i < permissions.length; i++) {
            if (permission.equals(permissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    private void onRunStart() {
        Log.d("RunTest", "Play button clicked");
        isRunning = true;
        startTimeMillis = System.currentTimeMillis(); // Updated line
        Log.d("RunTest", "Run started at: " + startTimeMillis); // Log the start time

        long currentDateMillis = System.currentTimeMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(currentDateMillis));

        // Request location permissions
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);

        // Call updateElapsedTime here
        updateElapsedTime();
    }


    private void updateElapsedTime() {
        // Update the TextView with the time
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
                    int seconds = (int) (elapsedMillis / 1000) % 60;
                    int minutes = (int) ((elapsedMillis / (1000 * 60)) % 60);
                    int hours = (int) ((elapsedMillis / (1000 * 60 * 60)) % 24);

                    String timeString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                    stopwatch.setText(timeString);

                    // Calculate pace in km/h
                    if (totalDistance > 0) {
                        float pace = (elapsedMillis / 1000) / (totalDistance / 1000);
                        float paceKmPerHour = (60 * 60) / pace;
                        String paceString = String.format(Locale.getDefault(), "%.2f km/h", paceKmPerHour);
                        averagePace.setText(paceString);
                    }

                    // Schedule the next update after 1 second
                    new Handler().postDelayed(this, 1000);
                }
            }
        });
    }

    private void onNewLocationReceived(Location location) {
        if (isRunning && map != null && routePolyline != null) {
            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            routePoints.add(newLatLng);
            drawRoutePolyline(); // Update the route polyline

            long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
            Log.d("PolylineTest", "New Location: " + newLatLng.latitude + ", " + newLatLng.longitude);
            Log.d("PolylineTest", "Polyline Points: " + routePoints.size());
            Log.d("PolylineTest", "Elapsed Time: " + elapsedMillis + " milliseconds");

            float distance = calculateDistance(newLatLng);
            totalDistance += distance;
            updateDistanceTextView();
        }
    }


    //When the fragment is currently on screen
    @Override
    public void onResume() {
        super.onResume();
        // Register the location update receiver
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(locationUpdateReceiver, new IntentFilter(LocationTrackingService.ACTION_LOCATION_UPDATE));
    }

    // When the fragment is no longer visible
    @Override
    public void onPause() {
        // Unregister the location update receiver
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(locationUpdateReceiver);
        super.onPause();
    }

    // Calculate the distance between the start and last point
    private float calculateDistance(LatLng newLatLng) {
        if (routePoints.size() > 1) {
            LatLng lastLatLng = routePoints.get(routePoints.size() - 2); // Get the last recorded location
            float[] results = new float[1];
            Location.distanceBetween(lastLatLng.latitude, lastLatLng.longitude, newLatLng.latitude, newLatLng.longitude, results);
            return results[0];
        }
        return 0;
    }


    private void updateDistanceTextView() {
        // Convert totalDistance from meters to kilometers
        float distanceInKm = totalDistance / 1000;

        // Update distance TextView  for the distance
        distanceCovered.setText(String.format(Locale.getDefault(), "%.2f km", distanceInKm));
    }

    private void onRunStop() {
        isRunning = false;
        Log.d("PolylineTest", "Run stopped");

        // Remove callbacks to stop updating the stopwatch
        new Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null);


        long endDateMillis = System.currentTimeMillis();

        float originalTotalDistance = totalDistance / 1000;


        totalDistance = 0;

        LatLng startLatLng = routePoints.isEmpty() ? null : routePoints.get(0);
        // Calculate and set the end coordinates
        LatLng endLatLng = routePoints.isEmpty() ? null : routePoints.get(routePoints.size() - 1);

        RunEntity runEntity = new RunEntity();
        runEntity.startTime = startTimeMillis;
        runEntity.endTime = endDateMillis;
        runEntity.distance = originalTotalDistance;
        runEntity.startLatitude = startLatLng != null ? startLatLng.latitude : 0.0;
        runEntity.startLongitude = startLatLng != null ? startLatLng.longitude : 0.0;
        runEntity.endLatitude = endLatLng != null ? endLatLng.latitude : 0.0;
        runEntity.endLongitude = endLatLng != null ? endLatLng.longitude : 0.0;

        // Calculate the duration in milliseconds
        long durationMillis = endDateMillis - startTimeMillis;

        // Convert duration from milliseconds to a formatted string
        String durationString = formatDuration(durationMillis);
        runEntity.duration = durationString; // Add duration to RunEntity

        // Use formattedDate instead of currentDateMillis
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        runEntity.formattedDate = dateFormat.format(new Date(endDateMillis));

        // Convert polyline points to a string using PolyLineConverter
        runEntity.polylinePoints = PolylineConverter.convertPolylineToString(routePoints);

        // Calculate pace in km/h
        float pace = (durationMillis / 1000) / originalTotalDistance; // Pace in seconds per kilometer
        float paceKmPerHour = (60 * 60) / pace;
        runEntity.pace = paceKmPerHour; // Add pace to RunEntity

        // Insert the RunEntity into the database using the RunRepository
        runRepository.insertRun(runEntity, new RunRepository.InsertRunCallback() {
            @Override
            public void onRunInserted(long runId) {
                Log.d("DatabaseTest", "Run added to the database. ID: " + runId);
            }
        });

        // Tests to check the data being collected
        Log.d("RunInfo", "Date: " + runEntity.formattedDate);
        Log.d("RunInfo", "Duration: " + durationString);
        Log.d("RunInfo", "Distance: " + originalTotalDistance + " km");
        Log.d("RunInfo", "Pace: " + paceKmPerHour + " km/h");
        Log.d("RunInfo", "Start Latitude: " + runEntity.startLatitude);
        Log.d("RunInfo", "Start Longitude: " + runEntity.startLongitude);
        Log.d("RunInfo", "End Latitude: " + runEntity.endLatitude);
        Log.d("RunInfo", "End Longitude: " + runEntity.endLongitude);
    }

    // Format the stopwatch so that it is in hours, minutes and seconds
    private String formatDuration(long durationMillis) {
        int seconds = (int) (durationMillis / 1000) % 60;
        int minutes = (int) ((durationMillis / (1000 * 60)) % 60);
        int hours = (int) ((durationMillis / (1000 * 60 * 60)) % 24);

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Informs the user the app needs to have the location permissions enabled to function
    private void showMissingPermissionError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Permission Denied")
                .setMessage("This app requires location permission to function properly.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    // Starts the LocationTrackingService
    private void startLocationService() {
        Intent serviceIntent = new Intent(requireContext(), LocationTrackingService.class);
        ContextCompat.startForegroundService(requireContext(), serviceIntent);
    }

    // Ends the LocationTrackingService
    private void stopLocationService() {
        Intent serviceIntent = new Intent(requireContext(), LocationTrackingService.class);
        requireContext().stopService(serviceIntent);
    }

    @Override
    public void onDestroy() {
        // Stop LocationTrackingService
        stopLocationService();
        super.onDestroy();
    }

    // Checks if a background service is running
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}