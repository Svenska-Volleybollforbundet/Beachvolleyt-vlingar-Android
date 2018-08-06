package com.ngusta.cupassist.activity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.CourtMarkerAdapter;
import com.ngusta.cupassist.domain.Court;
import com.ngusta.cupassist.service.CourtService;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

public class CourtsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener {

    public static final int PERMISSION_REQUEST_CODE = 1;

    private GoogleMap map;

    private CourtService courtService;

    private LocationManager locationManager;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CourtsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courts);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        requestLocationPermissions();
        courtService = ((MyApplication) getApplication()).getCourtService();
    }

    private void requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setInfoWindowAdapter(new CourtMarkerAdapter(getLayoutInflater()));
        map.setOnInfoWindowClickListener(this);
        initCamera();
        addMarkersForCourts();
    }

    private void initCamera() {
        LatLng sundsvall = new LatLng(62.398501, 17.293068);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sundsvall, 5);
        map.animateCamera(cameraUpdate);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1000F,
                    this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
            map.setMyLocationEnabled(true);
        }
    }

    private void addMarkersForCourts() {
        for (Court court : courtService.getCourts()) {
            LatLng position = new LatLng(court.getLat(), court.getLng());
            Marker marker = map.addMarker(new MarkerOptions().position(position));
            marker.setTag(court);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(map);
                }
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Court court = (Court) marker.getTag();
        if (court != null && court.hasLink()) {
            Uri cupAssistUri = Uri.parse(court.getLink());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, cupAssistUri);
            startActivity(browserIntent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
