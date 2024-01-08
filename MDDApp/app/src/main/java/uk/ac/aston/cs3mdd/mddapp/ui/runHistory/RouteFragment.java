package uk.ac.aston.cs3mdd.mddapp.ui.runHistory;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import uk.ac.aston.cs3mdd.mddapp.R;

public class RouteFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private RouteViewModel routeViewModel;
    private long selectedRunId;
    private GoogleMap googleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the selected run ID from the arguments
        if (getArguments() != null) {
            selectedRunId = getArguments().getLong("selectedRunId", 0);
        }

        routeViewModel = new ViewModelProvider(this).get(RouteViewModel.class);

        routeViewModel.getPolylineLiveData(selectedRunId).observe(this, polyline -> {
            drawPolylineOnMap(polyline);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_route, container, false);

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); // Set the OnMapReadyCallback

        return rootView;
    }

    private void drawPolylineOnMap(List<LatLng> polyline) {
        if (polyline != null && !polyline.isEmpty() && googleMap != null) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(ContextCompat.getColor(requireContext(), R.color.orange))
                    .width(10)
                    .addAll(polyline);

            googleMap.addPolyline(polylineOptions);
        } else {
            Log.e("RouteFragment", "googleMap is null or polyline is empty");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable the My Location button
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        // Observe changes in polyline data
        routeViewModel.getPolylineLiveData(selectedRunId).observe(getViewLifecycleOwner(), newPolyline -> {
            drawPolylineOnMap(newPolyline);

            // Adding markers
            if (googleMap != null && newPolyline != null && newPolyline.size() >= 2) {
                LatLng startPoint = newPolyline.get(0);
                LatLng endPoint = newPolyline.get(newPolyline.size() - 1);

                String runStats = getArguments().getString("runStats", "");

                TextView stats = getView().findViewById(R.id.runStats);

                stats.setText(runStats);

                googleMap.addMarker(new MarkerOptions()
                        .position(startPoint)
                        .title("Start of the Run")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                googleMap.addMarker(new MarkerOptions()
                        .position(endPoint)
                        .title("End of the Run")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 10));
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the back button press in the system bar
        if (item.getItemId() == android.R.id.home) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_route_to_run_history);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
