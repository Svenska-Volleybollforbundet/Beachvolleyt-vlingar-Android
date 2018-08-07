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
import com.ngusta.cupassist.domain.CourtWithKeyTag;
import com.ngusta.cupassist.service.CourtService;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CourtActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowLongClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    public static final int PERMISSION_REQUEST_CODE = 1;

    private GoogleMap map;

    private Map<String, Marker> markers;

    private CourtService courtService;

    private LocationManager locationManager;

    private boolean editEnabled = false;


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CourtActivity.class);
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

        makeActionOverflowMenuShown();
    }

    private void makeActionOverflowMenuShown() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, R.id.menu_item_edit_markers, Menu.NONE, R.string.enable_edit_markers)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(Menu.NONE, R.id.menu_item_normal_map, Menu.NONE, R.string.normal_map)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(Menu.NONE, R.id.menu_item_satellite, Menu.NONE, R.string.satellite)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_item_edit_markers:
                editEnabled = !editEnabled;
                if (editEnabled) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.edit_help)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                    item.setTitle(R.string.disable_edit_markers);
                } else {
                    item.setTitle(R.string.enable_edit_markers);
                }
                return true;
            case R.id.menu_item_normal_map:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.menu_item_satellite:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
        }
        return false;
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
        map.setOnInfoWindowLongClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMarkerDragListener(this);
        initCamera();
        markers = new HashMap<>();
        courtService.loadCourts(this);
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

    public void addMarkersForCourts(Map<String, Court> courts) {
        map.clear();
        for (Map.Entry<String, Court> entry : courts.entrySet()) {
            addMarkerForCourt(new CourtWithKeyTag(entry.getKey(), entry.getValue()));
        }
    }

    public void addMarkerForCourt(CourtWithKeyTag tag) {
        LatLng position = new LatLng(tag.court.getLat(), tag.court.getLng());
        Marker marker = map.addMarker(new MarkerOptions().position(position).draggable(true));
        marker.setTag(tag);
        markers.put(tag.key, marker);
    }

    public void removeMarker(String key) {
        Marker marker = markers.get(key);
        marker.remove();
        markers.remove(key);
    }

    public void updateMarker(String key, Court court) {
        Marker marker = markers.get(key);
        marker.setPosition(new LatLng(court.getLat(), court.getLng()));
        marker.setTag(new CourtWithKeyTag(key, court));
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
        CourtWithKeyTag tag = (CourtWithKeyTag) marker.getTag();
        if (tag != null && tag.court.hasLink()) {
            Uri cupAssistUri = Uri.parse(tag.court.getLink());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, cupAssistUri);
            startActivity(browserIntent);
        }
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        CourtWithKeyTag tag = (CourtWithKeyTag) marker.getTag();
        if (!editEnabled || tag == null) {
            return;
        }
        Toast.makeText(this, "Lååångt klick på " + tag.court.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (editEnabled) {
            courtService.addCourt(latLng.latitude, latLng.longitude);
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

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        CourtWithKeyTag tag = (CourtWithKeyTag) marker.getTag();
        if (tag == null) {
            return;
        }
        if (!editEnabled) {
            marker.setPosition(new LatLng(tag.court.getLat(), tag.court.getLng()));
        } else {
            courtService.updatePosition(tag.key, tag.court, marker.getPosition().latitude, marker.getPosition().longitude);
        }
    }
}
