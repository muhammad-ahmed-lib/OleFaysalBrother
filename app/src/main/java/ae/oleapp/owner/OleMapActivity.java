package ae.oleapp.owner;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.zego.ve.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.AutocompleteAdapter;
import ae.oleapp.adapters.v5DayAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityMapBinding;
import mumayank.com.airlocationlibrary.AirLocation;

public class OleMapActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener {

    private OleactivityMapBinding binding;
    private GoogleMap googleMap;
    private double lat = 0, lng = 0;
    private PlacesClient placesClient;
    private RecyclerView autocompleteRecyclerView;
    private AutocompleteAdapter autocompleteAdapter;
    private final List<AutocompletePrediction> predictionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, status, requestCode);
            if (dialog != null) dialog.show();
        } else {
            mapFragment.getMapAsync(this);
        }


        // Initialize the Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        }
        placesClient = Places.createClient(this);

        setupCustomSearchView();
        checkLocationPermissions();

        LinearLayoutManager dayLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.autocompleteRecyclerView.setLayoutManager(dayLayoutManager);
        autocompleteAdapter = new AutocompleteAdapter(getContext(), predictionList);
        autocompleteAdapter.setItemClickListener(predictionClickLister);
        binding.autocompleteRecyclerView.setAdapter(autocompleteAdapter);

        binding.btnDone.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    AutocompleteAdapter.ItemClickListener predictionClickLister = (view, pos) -> {
        if (predictionList != null && !predictionList.isEmpty()){
            moveCameraToPlace(predictionList.get(pos));
            binding.searchBar.setText("");
        }
    };



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnCameraIdleListener(this);
        this.googleMap.setOnMyLocationButtonClickListener(this);
        getLocation();
    }

    private void getLocation() {
        new AirLocation(this, true, true, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(Location loc) {
                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                lat = latLng.latitude;
                lng = latLng.longitude;
            }

            @Override
            public void onFailed(AirLocation.LocationFailedEnum locationFailedEnum) {
                // Handle the failure here
            }
        });
    }

    @Override
    public void onCameraIdle() {
        lat = googleMap.getCameraPosition().target.latitude;
        lng = googleMap.getCameraPosition().target.longitude;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        lat = googleMap.getCameraPosition().target.latitude;
        lng = googleMap.getCameraPosition().target.longitude;
        return false;
    }

    @Override
    public void onClick(View v) {
     if (v == binding.btnBack) {
            finish();
        }
    else if (v == binding.btnDone) {
        doneClicked();
    }
    }

    private void doneClicked() {
        if (lat == 0 || lng == 0) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setupCustomSearchView() {
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    getAutocompletePredictions(s.toString());
                } else {
                    predictionList.clear();
                    autocompleteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    private void getAutocompletePredictions(String query) {
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            predictionList.clear();
            predictionList.addAll(response.getAutocompletePredictions());
            autocompleteAdapter.notifyDataSetChanged();

        }).addOnFailureListener(e -> {
            Log.e("PlacesAPI", "Error fetching predictions: " + e.getMessage());
        });

    }


    private void moveCameraToPlace(AutocompletePrediction prediction) {
        String placeId = prediction.getPlaceId();
        // Create a FetchPlaceRequest
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, Collections.singletonList(Place.Field.LAT_LNG));

        placesClient.fetchPlace(request) // Use the created request
                .addOnSuccessListener(response -> {
                    Place place = response.getPlace();
                    LatLng selectedLatLng = place.getLatLng();
                    if (selectedLatLng != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 16));
                        lat = selectedLatLng.latitude;
                        lng = selectedLatLng.longitude;
                    }
                    predictionList.clear();
                    autocompleteAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Place Error", "An error occurred: " + e.getMessage()));
    }


    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        }
    }
}
