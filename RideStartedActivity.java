package com.aumento.onlinetruckbooking;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aumento.onlinetruckbooking.Utils.GlobalPreference;
import com.aumento.onlinetruckbooking.Utils.MapAnimator;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RideStartedActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "RideStartedActivity";

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Runnable mRunnable;
    private String ip;

    private ImageView driveIV, vehicleIV, driverIV;

    private TextView driverNameTV, driverVehicleNoTV, driverVehicleTV, driverPickupTimeTV;
    private TextView driverMsgTV, driverCallTV;

    private TextView driveNameTV, driveVehicleTV, driveRatingTV, driveVehicleNoTV;
    private double olat;
    private double olon;
    private double dlat;
    private double dlon;

    private List<LatLng> route;
    LatLng POINT_A;
    LatLng POINT_B;

    private Location currentLocation;

    private ConstraintLayout riderStartDetails, driverCL;
    private Handler m_Handler;
    private String did;
    private String rid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_started);

        GlobalPreference globalPreference = new GlobalPreference(this);
        ip = globalPreference.RetriveIP();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map6);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        olat = intent.getDoubleExtra("olat",0);
        olon = intent.getDoubleExtra("olon",0);
        dlat = intent.getDoubleExtra("dlat",0);
        dlon = intent.getDoubleExtra("dlon",0);
        did = intent.getStringExtra("did");
        rid = intent.getStringExtra("rid");

        POINT_A = new LatLng(olat,olon);
        POINT_B = new LatLng(dlat,dlon);

        init();

        getDriverDetails();

        m_Handler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {

                getDriverLocation();

                m_Handler.postDelayed(mRunnable, 5000);// move this inside the run method
            }
        };
        mRunnable.run();
    }

    private void init() {

        driveIV = (ImageView) findViewById(R.id.driveImageView);
        vehicleIV = (ImageView) findViewById(R.id.vehicleImageView);
        driverIV = (ImageView) findViewById(R.id.driverImageView);


//        Glide.with(RideStartedActivity.this).load(getResources()
//                .getIdentifier("round_rect", "drawable", getApplicationContext().getPackageName())).into(driverIV);

        driverNameTV = (TextView) findViewById(R.id.driverNameTextView);
        driverVehicleNoTV = (TextView) findViewById(R.id.driverVehicleNoTextView);
        driverVehicleTV = (TextView) findViewById(R.id.driverVehicleTextView);
        driverPickupTimeTV = (TextView) findViewById(R.id.driverPickupTimeTextView);
        driverMsgTV = (TextView) findViewById(R.id.driverMsgTextView);
        driverCallTV = (TextView) findViewById(R.id.driverCallTextView);

        driveNameTV = (TextView) findViewById(R.id.driveNameTextView);
        driveVehicleTV = (TextView) findViewById(R.id.driveVehicleTextView);
        driveRatingTV = (TextView) findViewById(R.id.driveRatingTextView);
        driveVehicleNoTV = (TextView) findViewById(R.id.driveVehicleNoTextView);

        riderStartDetails = (ConstraintLayout) findViewById(R.id.riderStartDetails);
        driverCL = (ConstraintLayout) findViewById(R.id.driverCL);

    }

    private void getDriverLocation() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/getDriverLocation.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: getDriverLocation"+response);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String trip_status = object.getString("trip_status");
                        double latitude = object.getDouble("latitude");
                        double longitude = object.getDouble("longitude");

                        if(trip_status.equals("on_trip"))
                        {
                            riderStartDetails.setVisibility(View.VISIBLE);
                            driverCL.setVisibility(View.GONE);
                        }
                        else if(trip_status.equals("completed"))
                        {
                          m_Handler.removeCallbacks(mRunnable);
                          rideFinishedDetails();
                        }

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
                Log.d(TAG, "onErrorResponse: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("did",did);
                params.put("rid",rid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }

    private void rideFinishedDetails() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/getTripCompletedDetails.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: getDriverDetails"+response);
                Intent intent = new Intent(RideStartedActivity.this,RideFinishActivity.class);
                intent.putExtra("response",response);
                startActivity(intent);
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("rid",rid);
                params.put("did",did);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void getDriverDetails() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/getDriverDetails.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: getDriverDetails"+response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String name = object.getString("name");
                        String phone = object.getString("phone");
                        String vehicle_no = object.getString("vehicle_no");
                        String vehicle_model = object.getString("vehicle_model");
                        String driver_image = object.getString("driver_image");
                        String image = object.getString("image");

                        driverNameTV.setText(name);
                        driveNameTV.setText(name);

                        Glide.with(RideStartedActivity.this).load("http://"+ip+"/cab_booking/API/company/tbl_driver/uploads/"+driver_image)
                                .circleCrop()
                                .into(driverIV);

                        Glide.with(RideStartedActivity.this).load("http://"+ip+"/cab_booking/API/company/tbl_driver/uploads/"+driver_image)
                                .circleCrop()
                                .into(driveIV);

                        Glide.with(getApplicationContext()).load("http://"+ip+"/cab_booking/API/admin/tbl_vehicle_type/uploads/"+image)
                                .circleCrop()
                                .into(vehicleIV);

                        Log.d(TAG, "IMG: "+"http://"+ip+"/cab_booking/API/company/tbl_driver/uploads/"+driver_image+" \n"+
                                "http://"+ip+"/cab_booking/API/admin/tbl_vehicle_type/uploads/"+image);

                        driverVehicleNoTV.setText(vehicle_no);
                        driveVehicleNoTV.setText(vehicle_no);

                        driveVehicleTV.setText(vehicle_model);
                        driverVehicleTV.setText(vehicle_model);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("did",did);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



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

                /*if(first) {
                    origin = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            origin).zoom(15).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    first = false;

//                    getVehicleNearBy();
                }*/

            }
        });

        LatLng origin = new LatLng(olat, olon);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                origin).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

        createRoute();

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(POINT_A);
                builder.include(POINT_B);
                LatLngBounds bounds = builder.build();
//                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
//                mMap.moveCamera(cu);
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                startAnim();
            }
        });


    }

    private void createRoute() {
        if (route == null) {
            route = new ArrayList<>();
        } else {
            route.clear();
        }

        route.add(new LatLng(10.002522069458275, 76.30623611112632));
        route.add(new LatLng(10.005241121973151, 76.30508778913985));
        route.add(new LatLng(10.015888746554884, 76.30168533811478));
        route.add(new LatLng(10.019909437060207, 76.30047557775032));
        route.add(new LatLng(10.019388239260353, 76.29064627478903));
        route.add(new LatLng(10.018122469687768, 76.28444625292114));
        route.add(new LatLng(10.014027298994653, 76.2857316233084));
        route.add(new LatLng(10.00665586151376, 76.2882267540601));
        route.add(new LatLng(9.994965461023058, 76.29208286522184));
        route.add(new LatLng(9.998614102503836, 76.2965438565658));
        route.add(new LatLng(10.001741476884662, 76.30349997866148));


    }

    private void startAnim() {
        if (mMap != null) {
            MapAnimator.getInstance().animateRoute(mMap, route);
        }    else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
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
}