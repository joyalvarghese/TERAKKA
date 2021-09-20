package com.aumento.onlinetruckbooking;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DestinationMarkerActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    LatLng origin;
    private Location currentLocation;
    private ImageView currentLocationIV;
    private boolean first = true;
    private TextView doneButtonTV;
    private ImageView markerIV;
    private EditText originET, destinationET;
    private double deslat;
    private double deslon;
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_marker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("latitude",0);
        lon = intent.getDoubleExtra("longitude",0);

        originET.setText(getAddress(lat,lon));
    }

    private void init() {

        currentLocationIV = (ImageView) findViewById(R.id.currentLocationImageView);
        currentLocationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!String.valueOf(currentLocation.getLatitude()).equals("") && !String.valueOf(currentLocation.getLongitude()).equals(""))
                {

                    origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            origin).zoom(15).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    getAddress(currentLocation.getLatitude(),currentLocation.getLongitude());

                }
            }
        });

        doneButtonTV = (TextView) findViewById(R.id.doneButtonTV);
        originET = (EditText) findViewById(R.id.originEditText);
        destinationET = (EditText) findViewById(R.id.destinationEditText);
        markerIV = (ImageView) findViewById(R.id.markerIV);

        doneButtonTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DestinationMarkerActivity.this,RequestBookingActivity.class);
                intent.putExtra("olat",lat);
                intent.putExtra("olon",lon);
                intent.putExtra("dlat",deslat);
                intent.putExtra("dlon",deslon);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                currentLocation = location;

                if(first) {
                    origin = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            origin).zoom(15).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    first = false;
                }

                getAddress(location.getLatitude(),location.getLongitude());

            }
        });


        // Customise the styling of the base map using a JSON object defined
        // in a raw resource file.
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
            if (!success) {
                Log.e("MapActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapActivity", "Can't find style. Error: ", e);
        }

        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
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

    public String getAddress(double lat, double lng) {

        Geocoder geocoder = new Geocoder(DestinationMarkerActivity.this, Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);

            String[] splited = add.split(",");
            address = splited[0] + ", " + splited[1] + "\n" + splited[2] + ", " + splited[3] + ", " + splited[4];

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    @Override
    public void onCameraIdle() {
        MarkerOptions markerOptions = new MarkerOptions().position(mMap.getCameraPosition().target);
        destinationET.setText(getAddress(markerOptions.getPosition().latitude,markerOptions.getPosition().longitude));
        deslat = markerOptions.getPosition().latitude;
        deslon = markerOptions.getPosition().longitude;
    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {
        mMap.clear();
    }

}