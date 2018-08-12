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
import com.ngusta.cupassist.domain.OnMarkerChangeListener;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.ngusta.cupassist.domain.CourtWithKeyTag.getTag;

public class CourtActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, OnMarkerChangeListener, GoogleMap.OnMarkerClickListener {

    public static final int PERMISSION_REQUEST_CODE = 1;

    private GoogleMap map;

    private Map<String, Marker> markers;

    private CourtService courtService;

    private LocationManager locationManager;

    private boolean editEnabled = false;

    private Marker lastSelectedMarker;

    private CommonActivityHelper commonActivityHelper;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CourtActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courts);

        commonActivityHelper = new CommonActivityHelper(this);
        commonActivityHelper.initToolbarAndNavigation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        requestLocationPermissions();
        courtService = ((MyApplication) getApplication()).getCourtService();

        makeActionOverflowMenuShown();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        commonActivityHelper.syncDrawerToggleState();
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
                    CourtDialogs.showEditInfoDialog(this, getPreferences(MODE_PRIVATE));
                    item.setTitle(R.string.disable_edit_markers);
                } else {
                    item.setTitle(R.string.enable_edit_markers);
                }
                if (lastSelectedMarker != null && lastSelectedMarker.isInfoWindowShown()) {
                    lastSelectedMarker.showInfoWindow();
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
        map.setInfoWindowAdapter(new CourtMarkerAdapter(getLayoutInflater(), this));
        map.setOnInfoWindowClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMarkerDragListener(this);
        map.setOnMarkerClickListener(this);
        CourtDialogs.showWelcomeDialog(this, getPreferences(MODE_PRIVATE));
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

    public boolean isEditEnabled() {
        return editEnabled;
    }

    @Override
    public void addMarkers(Map<String, Court> courts) {
        map.clear();
        markers.clear();
        for (Map.Entry<String, Court> entry : courts.entrySet()) {
            addMarker(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void addMarker(String key, Court court) {
        if (!markers.containsKey(key)) {
            LatLng position = new LatLng(court.getLat(), court.getLng());
            Marker marker = map.addMarker(new MarkerOptions().position(position).draggable(true));
            marker.setTag(new CourtWithKeyTag(key, court));
            markers.put(key, marker);
        }
    }

    @Override
    public void removeMarker(String key) {
        Marker marker = markers.get(key);
        if (marker != null) {
            marker.remove();
            markers.remove(key);
        }
    }

    @Override
    public void updateMarker(String key, Court court) {
        Marker marker = markers.get(key);
        if (marker != null) {
            marker.setPosition(new LatLng(court.getLat(), court.getLng()));
            marker.setTag(new CourtWithKeyTag(key, court));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCamera();
                }
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (editEnabled) {
            CourtDialogs.showEditCourtInfoDialog(this, courtService, marker);
        } else {
            CourtWithKeyTag tag = getTag(marker);
            if (tag.court.hasValidLink()) {
                Uri uri = Uri.parse(tag.court.getLink());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (editEnabled) {
            CourtDialogs.showConfirmCreateMarkerDialog(this, courtService, latLng.latitude, latLng.longitude);
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
        CourtWithKeyTag tag = getTag(marker);
        if (!editEnabled) {
            marker.setPosition(new LatLng(tag.court.getLat(), tag.court.getLng()));
        } else {
            CourtDialogs.showConfirmMoveMarkerDialog(this, courtService, tag, marker);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        lastSelectedMarker = marker;
        return false;
    }
}
