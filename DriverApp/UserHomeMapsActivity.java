package com.aumento.onlinecabdriver;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aumento.onlinecabdriver.Utils.GlobalPreference;
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
    private TextView currentAddressTV;
    private String ip;
    private Runnable mRunnable;
    private Handler handler;
    private Runnable myRunnable;
    private String uid;
    private String id;
    private AlertDialog show;

    private TextView customerNameTV, customerPickupTV, customerMsgTV, customerCallTV, startRideTV, nameTV, finishRideTV;
    private ConstraintLayout customerCL, riderStartDetails;
    private LinearLayout ll;
    private String phone;
    private double olat;
    private double olon;
    private String trip_status = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_maps);

        GlobalPreference globalPreference = new GlobalPreference(this);
        ip = globalPreference.RetriveIP();
        uid = globalPreference.RetriveUID();

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

        Intent intent = getIntent();
        if(intent.hasExtra("trip_status"))
        {
            trip_status = intent.getStringExtra("trip_status");

            olat = intent.getDoubleExtra("olat", 0);
            olon = intent.getDoubleExtra("olon", 0);
            double dlat = intent.getDoubleExtra("dlat", 0);
            double dlon = intent.getDoubleExtra("dlon", 0);
            String start_loc = intent.getStringExtra("start_loc");
            uid = intent.getStringExtra("did");
            id = intent.getStringExtra("rid");

            String name = intent.getStringExtra("first_name");
            phone = intent.getStringExtra("phone_number");

            customerNameTV.setText(name);
            customerPickupTV.setText(start_loc);
            nameTV.setText(name);

            if(intent.getStringExtra("trip_status").equals("waiting"))
            {
                customerCL.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                riderStartDetails.setVisibility(View.GONE);
            }
            else if(intent.getStringExtra("trip_status").equals("on_trip"))
            {
                customerCL.setVisibility(View.GONE);
                riderStartDetails.setVisibility(View.VISIBLE);
            }
        }

    }

    private void init() {

        customerNameTV = (TextView) findViewById(R.id.customerNameTextView);
        customerPickupTV = (TextView) findViewById(R.id.customerPickupTextView);
        customerMsgTV = (TextView) findViewById(R.id.customerMsgTextView);
        customerCallTV = (TextView) findViewById(R.id.customerCallTextView);
        startRideTV = (TextView) findViewById(R.id.startRideTextView);
        nameTV = (TextView) findViewById(R.id.nameTextView);
        finishRideTV = (TextView) findViewById(R.id.finishRideTextView);

        ImageView tripHistoryIV = (ImageView) findViewById(R.id.tripHistoryImageView);
        tripHistoryIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomeMapsActivity.this,TripHistoryActivity.class));
            }
        });

        ImageView profileIV = (ImageView) findViewById(R.id.profileImageView);
        profileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomeMapsActivity.this,ProfileActivity.class));
            }
        });

        customerMsgTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"+phone));
            }
        });
        customerCallTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });


        customerCL = (ConstraintLayout) findViewById(R.id.customerCL);
        riderStartDetails = (ConstraintLayout) findViewById(R.id.riderStartDetails);
        ll = (LinearLayout) findViewById(R.id.ll);

        startRideTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRide();
            }
        });
        finishRideTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishRide();
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

        currentAddressTV = (TextView) findViewById(R.id.currentAddressTextView);

        handler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(show != null)
                {
                    show.cancel();
                }
                checkRequests();
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

    private void finishRide() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + ip + "/cab_booking/API/driverFinishRide.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                Intent intent = new Intent(UserHomeMapsActivity.this,FinishRideActivity.class);
                intent.putExtra("response",response);
                finish();
                startActivity(intent);
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
                params.put("uid", uid);
                params.put("rid", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void startRide() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + ip + "/cab_booking/API/driverStartRide.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);

                customerCL.setVisibility(View.GONE);
                riderStartDetails.setVisibility(View.VISIBLE);
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
                params.put("uid", uid);
                params.put("rid", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void checkRequests() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + ip + "/cab_booking/API/checkDriverIncomingRequests.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("Driver Inactive"))
                    Log.d(TAG, "DriverIncomingRequests : Driver Inactive ");
                else if(response.equals("failed"))
                    Log.d(TAG, "DriverIncomingRequests : No incoming Request");
                else{

                    Log.d(TAG, "onResponse: " +
                            "" + response);

                    handler.removeCallbacks(mRunnable);

                    String name = null;
                    String start_location = null;
                    String dest_location = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            id = object.getString("id");
                            name = object.getString("name");
                            phone = object.getString("phone");
                            start_location = object.getString("start_location");
                            dest_location = object.getString("dest_location");
                            String start_lat = object.getString("start_lat");
                            String start_lon = object.getString("start_lon");
                            String dest_lat = object.getString("dest_lat");
                            String dest_lon = object.getString("dest_lon");
                            String trip_date = object.getString("trip_date");
                            String start_time = object.getString("start_time");

                            customerNameTV.setText(name);
                            customerPickupTV.setText(start_location);
                            nameTV.setText(name);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(UserHomeMapsActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogLayout = inflater.inflate(R.layout.raw_request_alert, null);
                    TextView customerNameTV = dialogLayout.findViewById(R.id.customerNameTextView);
                    TextView customerPickupTV = dialogLayout.findViewById(R.id.customerPickupTextView);
                    TextView customerDropoffTV = dialogLayout.findViewById(R.id.customerDropoffTextView);
                    TextView acceptTV = dialogLayout.findViewById(R.id.acceptTextView);


                    customerNameTV.setText(name);
                    customerPickupTV.setText(start_location);
                    customerDropoffTV.setText(dest_location);
                    acceptTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            acceptRequest();
                        }
                    });
                    builder.setView(dialogLayout);

                    show = builder.show();
                    show.setCancelable(false);
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
                params.put("uid", uid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    private void acceptRequest() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + ip + "/cab_booking/API/driverAcceptRequest.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                show.cancel();

                customerCL.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                riderStartDetails.setVisibility(View.GONE);
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
                params.put("uid", uid);
                params.put("rid", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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

                updateLocation(location.getLatitude(),location.getLongitude());
                getAddress(location.getLatitude(),location.getLongitude());

            }
        });

        if(trip_status.equals("waiting"))
        {
            BitmapDescriptor icon5 = BitmapDescriptorFactory.fromResource(R.drawable.home_location);
            LatLng sydney5 = new LatLng(olat, olon);
            MarkerOptions markerOptions5 = new MarkerOptions().position(sydney5)
                    .title("Marker in Bharuch")
                    .snippet("snippet snippet snippet snippet snippet...")
                    .icon(icon5);
            mMap.addMarker(markerOptions5);
        }
        else if(trip_status.equals("on_trip"))
        {
            BitmapDescriptor icon5 = BitmapDescriptorFactory.fromResource(R.drawable.home_location);
            LatLng sydney5 = new LatLng(olat, olon);
            MarkerOptions markerOptions5 = new MarkerOptions().position(sydney5)
                    .title("Marker in B")
                    .snippet("snippet snippet snippet snippet snippet...")
                    .icon(icon5);
            mMap.addMarker(markerOptions5);
        }

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

    private void updateLocation(double latitude, double longitude) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + ip + "/cab_booking/API/updateDriverLocation.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);

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
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("uid", uid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


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
            /*add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getSubLocality();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();*/

            String[] splited = add.split(",");

            currentAddressTV.setText(splited[0]+", "+splited[1]+"\n"+splited[2]+", "+splited[3]+", "+splited[4]);

            Log.v("IGA", "Address" + add);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}