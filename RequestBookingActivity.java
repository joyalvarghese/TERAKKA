package com.aumento.onlinetruckbooking;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.aumento.onlinetruckbooking.adapter.VehicleListRecycleAdapter;
import com.aumento.onlinetruckbooking.model.VehicleRequestListModelClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RequestBookingActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "RequestBookingActivity";

    ArrayList<VehicleRequestListModelClass> vehicleLists;
    private RecyclerView VehicleRequestListRV;
    private String ip;
    private GoogleMap mMap;
    private double olat;
    private double olon;
    private double dlat;
    private double dlon;

    private TextView sendRequestTV, rtext;
    private VehicleListRecycleAdapter adapter;
    private GlobalPreference globalPreference;
    private String vehicleType;
    private Handler handler;
    private Runnable myRunnable;

    ArrayList<MarkerOptions> arrayList = new ArrayList<>();
    private RoundedHorizontalProgressBar mRoundedHorizontalProgressBar1;
    private LinearLayout routeLL, rb;
    private EditText originEditText, destinationEditText;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_booking);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map4);
        mapFragment.getMapAsync(this);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.RetriveIP();

        Intent intent = getIntent();
        olat = intent.getDoubleExtra("olat",0);
        olon = intent.getDoubleExtra("olon",0);
        dlat = intent.getDoubleExtra("dlat",0);
        dlon = intent.getDoubleExtra("dlon",0);

        VehicleRequestListRV = (RecyclerView) findViewById(R.id.VehicleRequestListRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RequestBookingActivity.this);
        VehicleRequestListRV.setLayoutManager(layoutManager);
        VehicleRequestListRV.setItemAnimator(new DefaultItemAnimator());

        vehicleTypeList();

        sendRequestTV = (TextView) findViewById(R.id.sendRequestTextView);
        sendRequestTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getSelected().size() > 0) {
                    for (int i = 0; i < adapter.getSelected().size(); i++) {
                        vehicleType = adapter.getSelected().get(i).getVehicle_type();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Selection", Toast.LENGTH_SHORT).show();
                }
                sendRequest();
            }
        });

        mRoundedHorizontalProgressBar1 = (RoundedHorizontalProgressBar) findViewById(R.id.progress_bar_1);
        mRoundedHorizontalProgressBar1.setVisibility(View.GONE);
        routeLL = (LinearLayout) findViewById(R.id.routeLL);
        routeLL.setVisibility(View.GONE);
        rb = (LinearLayout) findViewById(R.id.rb);
        rtext = (TextView) findViewById(R.id.rtext);
        rtext.setVisibility(View.GONE);
        originEditText = (EditText) findViewById(R.id.originEditText);
        destinationEditText = (EditText) findViewById(R.id.destinationEditText);

        originEditText.setText(getAddress(olat,olon));
        destinationEditText.setText(getAddress(dlat,dlon));
    }

    public void requestVehicles(String vehicle_type){

        mMap.clear();
        Log.d(TAG, "requestVehicles: "+vehicle_type+"   "+arrayList.size());

        BitmapDescriptor icon3 = BitmapDescriptorFactory.fromResource(R.drawable.location_one);
        LatLng sydney3 = new LatLng(olat, olon);
        MarkerOptions markerOptions3 = new MarkerOptions().position(sydney3)
                .title("Marker in Bharuch")
                .snippet("snippet snippet snippet snippet snippet...")
                .icon(icon3);
        mMap.addMarker(markerOptions3);


        BitmapDescriptor icon5 = BitmapDescriptorFactory.fromResource(R.drawable.home_location);
        LatLng sydney5 = new LatLng(dlat, dlon);
        MarkerOptions markerOptions5 = new MarkerOptions().position(sydney5)
                .title("Marker in Bharuch")
                .snippet("snippet snippet snippet snippet snippet...")
                .icon(icon5);
        mMap.addMarker(markerOptions5);


        for(int i = 0; i < arrayList.size(); i++)
        {
            MarkerOptions markerOptions = arrayList.get(i);
            Marker myMarker = mMap.addMarker(markerOptions);
            myMarker.remove();
        }

        if(handler != null)
        {
            handler.removeCallbacks(myRunnable);
        }

        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                mRoundedHorizontalProgressBar1.animateProgress(5000, 0,100);
                getAllVehicleByType(vehicle_type);
                handler.postDelayed(myRunnable, 5000);// move this inside the run method
            }
        };
        myRunnable.run();

    }

    private void getAllVehicleByType(String vehicleType) {

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car);
        arrayList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/getVehicleListByType.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject object = jsonArray.getJSONObject(i);
                        double latitude = object.getDouble("latitude");
                        double longitude = object.getDouble("longitude");

                        arrayList.add(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .icon(icon));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for(int i = 0; i < arrayList.size(); i++)
                {
                    mMap.addMarker(arrayList.get(i));
                }

               /* Intent intent = new Intent(RequestBookingActivity.this,RideStartedActivity.class);
                intent.putExtra("olat",olat);
                intent.putExtra("olon",olon);
                intent.putExtra("dlat",dlat);
                intent.putExtra("dlon",dlon);
                startActivity(intent);*/

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
                params.put("startlat", String.valueOf(olat));
                params.put("startlon", String.valueOf(olon));
                params.put("vehicleType", vehicleType);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void sendRequest() {

        String startLoc = getLocation(olat,olon);
        String destLoc = getLocation(dlat,dlon);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/sendRequest.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: sendRequest" + response);

               requesting(response);

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
                params.put("uid",globalPreference.RetriveUID());
                params.put("startLoc", String.valueOf(startLoc));
                params.put("destLoc", String.valueOf(destLoc));
                params.put("startlat", String.valueOf(olat));
                params.put("startlon", String.valueOf(olon));
                params.put("destlat", String.valueOf(dlat));
                params.put("destlon", String.valueOf(dlon));
                params.put("payment", "cash");
                params.put("vehicleType", vehicleType);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void requesting(String response) {

        routeLL.setVisibility(View.VISIBLE);
        rb.setVisibility(View.GONE);
        rtext.setVisibility(View.VISIBLE);
        VehicleRequestListRV.setVisibility(View.GONE);
        mRoundedHorizontalProgressBar1.setVisibility(View.VISIBLE);
        mRoundedHorizontalProgressBar1.setElevation(10);

        sendRequestTV.setText("Cancel Request");

        String rid = "";
        if (response.equals("failed")) {

            Toast.makeText(RequestBookingActivity.this, "Please try Agin", Toast.LENGTH_SHORT).show();

        } else {

            handler.removeCallbacks(myRunnable);

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("da");
                JSONObject object = jsonArray.getJSONObject(0);
                rid = object.getString("rid");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        startActivity(intent);

        handler = new Handler();
        String finalRid = rid;
        myRunnable = new Runnable() {
            @Override
            public void run() {
                mRoundedHorizontalProgressBar1.animateProgress(5000, 0,100);
                checkRequestStatus(finalRid);
                handler.postDelayed(myRunnable, 5000);// move this inside the run method
            }
        };
        myRunnable.run();

        this.sendRequestTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequest(finalRid);
            }
        });

    }

    private void cancelRequest(String finalRid) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/cancelRequest.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: sendRequest" + response);
                if(response.equals("canceled"))
                {
                    handler.removeCallbacks(myRunnable);
                    finish();
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
                params.put("rid",finalRid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void checkRequestStatus(String finalRid) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/userRequesting.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: sendRequest" + response);
                if(!response.equals("failed"))
                {
                handler.removeCallbacks(myRunnable);

                    String did = null;
                    String rid = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("da");
                        JSONObject object = jsonArray.getJSONObject(0);
                        did = object.getString("did");
                        rid = object.getString("rid");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    intent = new Intent(RequestBookingActivity.this, RideStartedActivity.class);
                    intent.putExtra("olat", olat);
                    intent.putExtra("olon", olon);
                    intent.putExtra("dlat", dlat);
                    intent.putExtra("dlon", dlon);
                    intent.putExtra("did", did);
                    intent.putExtra("rid", rid);
                    startActivity(intent);
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
                params.put("rid",finalRid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        BitmapDescriptor icon3 = BitmapDescriptorFactory.fromResource(R.drawable.location_one);
        LatLng sydney3 = new LatLng(olat, olon);
        MarkerOptions markerOptions3 = new MarkerOptions().position(sydney3)
                .title("Marker in Bharuch")
                .snippet("snippet snippet snippet snippet snippet...")
                .icon(icon3);
        mMap.addMarker(markerOptions3);


        BitmapDescriptor icon5 = BitmapDescriptorFactory.fromResource(R.drawable.home_location);
        LatLng sydney5 = new LatLng(dlat, dlon);
        MarkerOptions markerOptions5 = new MarkerOptions().position(sydney5)
                .title("Marker in Bharuch")
                .snippet("snippet snippet snippet snippet snippet...")
                .icon(icon5);
        mMap.addMarker(markerOptions5);

        LatLng origin = new LatLng(olat, olon);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                origin).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
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

    public String getLocation(double lat, double lng) {

        Geocoder geocoder = new Geocoder(RequestBookingActivity.this, Locale.getDefault());
        String location = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);

            location = obj.getSubLocality();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return location;
    }

    private void vehicleTypeList() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/getVehicleTypeList.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);
                vehicleLists = new ArrayList<>();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String id = object.getString("id");
                        String type = object.getString("type");
                        String capacity = object.getString("capacity");
                        String rate = object.getString("rate");
                        String image = object.getString("image");

                        vehicleLists.add(new VehicleRequestListModelClass(id,type,"11:20pm",rate,capacity,image));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                adapter = new VehicleListRecycleAdapter(RequestBookingActivity.this, vehicleLists);
                VehicleRequestListRV.setAdapter(adapter);

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
                params.put("slat", String.valueOf(olat));
                params.put("slon", String.valueOf(olon));
                params.put("dlat", String.valueOf(dlat));
                params.put("dlon", String.valueOf(dlon));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


     /*   Dialog slideDialog = new Dialog(RequestBookingActivity.this, R.style.CustomDialogAnimation);
        slideDialog.setContentView(R.layout.select_vehicle_list_popup);


        slideDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Setting dialogview
        Window window = slideDialog.getWindow();
        //  window.setGravity(Gravity.BOTTOM);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        slideDialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
        layoutParams.copyFrom(slideDialog.getWindow().getAttributes());

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.60);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = 550;
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.y = 570;


        RecyclerView vehicleListRV = (RecyclerView) slideDialog.findViewById(R.id.vehicleListRecyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RequestBookingActivity.this);
        vehicleListRV.setLayoutManager(layoutManager);
        vehicleListRV.setItemAnimator(new DefaultItemAnimator());*/



        /*VehicleListRecycleAdapter adapter = new VehicleListRecycleAdapter(RequestBookingActivity.this, vehicleLists);
        vehicleListRV.setAdapter(adapter);

        slideDialog.getWindow().setAttributes(layoutParams);
        slideDialog.setCancelable(true);
        slideDialog.setCanceledOnTouchOutside(true);
        slideDialog.show();
*/



      /*  VehicleRequestListRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(RequestBookingActivity.this, "asdasdasd", Toast.LENGTH_SHORT).show();

                ViewGroup.LayoutParams params=VehicleRequestListRV.getLayoutParams();
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                VehicleRequestListRV.setLayoutParams(params);

            }
        });
*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(myRunnable);
    }

    public String getAddress(double lat, double lng) {

        Geocoder geocoder = new Geocoder(RequestBookingActivity.this, Locale.getDefault());
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
}