package com.aumento.onlinetruckbooking;

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
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aumento.onlinetruckbooking.Utils.GlobalPreference;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserHomeMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "UserHomeMapsActivity";

    private GoogleMap mMap;
    private LocationManager locationManager;
    LatLng origin, destination;
    private Location currentLocation;
    private ImageView currentLocationIV;
    private GoogleApiClient googleApiClient;
    private boolean first = true;
    private TextView currentAddressET;
    private TextView currentAddressTV;
    private String ip;
    private Runnable mRunnable;
    private Handler handler;
    private Runnable myRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_maps);

        GlobalPreference globalPreference = new GlobalPreference(this);
        ip = globalPreference.RetriveIP();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();


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


    }

    private void getVehicleNearBy() {

        if (mMap != null)
            mMap.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + ip + "/cab_booking/API/getNearByVehicle.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String id = object.getString("id");
                        double latitude = object.getDouble("latitude");
                        double longitude = object.getDouble("longitude");

                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car);
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .icon(icon));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("latitude", String.valueOf(currentLocation.getLatitude()));
                params.put("longitude", String.valueOf(currentLocation.getLongitude()));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void init() {

        ImageView tripHistoryIV = (ImageView) findViewById(R.id.tripHistoryImageView);
        tripHistoryIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserHomeMapsActivity.this,HistoryActivity.class));
            }
        });

        ImageView profileIV = (ImageView) findViewById(R.id.profileImageView);
        profileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserHomeMapsActivity.this,ProfileActivity.class));
            }
        });

        currentLocationIV = (ImageView) findViewById(R.id.currentLocationImageView);
        currentLocationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!String.valueOf(currentLocation.getLatitude()).equals("") && !String.valueOf(currentLocation.getLongitude()).equals("")) {

                    origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            origin).zoom(15).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    getAddress(currentLocation.getLatitude(), currentLocation.getLongitude());

                }
            }
        });

        currentAddressET = (TextView) findViewById(R.id.currentAddressEditText);
        currentAddressTV = (TextView) findViewById(R.id.currentAddressTextView);

        currentAddressET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handler.removeCallbacks(mRunnable);

                Intent intent = new Intent(UserHomeMapsActivity.this, DestinationActivity.class);
                intent.putExtra("latitude", currentLocation.getLatitude());
                intent.putExtra("longitude", currentLocation.getLongitude());
                startActivity(intent);
            }
        });


        handler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                getVehicleNearBy();
                handler.postDelayed(mRunnable, 4000);// move this inside the run method
            }
        };
        mRunnable.run();

        this.findViewById(R.id.hamPopupMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(getApplicationContext(), v);
                menu.getMenu().add(Menu.NONE, 1, 1, "Logout");
                menu.show();

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int i = item.getItemId();
                        if (i == 1) {
                            GlobalPreference globalPreference = new GlobalPreference(UserHomeMapsActivity.this);
                            globalPreference.setLoginStatus(false);
                            startActivity(new Intent(UserHomeMapsActivity.this,LoginActivity.class));
                            finish();
                            return true;
                        }  else {
                            return false;
                        }
                    }

                });
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Toast.makeText(this, "Map Ready", Toast.LENGTH_SHORT).show();

        if (currentLocation != null && first) {
            origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    origin).zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            first = false;
        }

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

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("MapActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapActivity", "Can't find style. Error: ", e);
        }

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

    public void getAddress(double lat, double lng) {

        Log.v("IGA", "Address" + lat+"  "+lng);

        Geocoder geocoder = new Geocoder(UserHomeMapsActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            Log.d(TAG, "getAddress: "+add);
            /*add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getSubLocality();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();*/

            String[] splited = add.split(",");

//            currentAddressET.setText(obj.getSubLocality());
            currentAddressTV.setText(add);

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(mRunnable);
        Log.d(TAG, "onPause: paused user home ");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: user home resumed");

        handler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                getVehicleNearBy();
                handler.postDelayed(mRunnable, 4000);// move this inside the run method
            }
        };
        mRunnable.run();

    }


}