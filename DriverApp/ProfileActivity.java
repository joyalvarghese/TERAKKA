package com.aumento.onlinecabdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.aumento.onlinecabdriver.Utils.GlobalPreference;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    TextView driverNameTV, driverVehicleNoTV, driverVehicleModelTV, driverVehicleTypeTV, driverMobileTV;

    private Intent intent;
    String intentResponse;
    GlobalPreference globalPreference;
    private String ip;
    private String uid;
    private String image = "";

    private ImageView userIV;
    private SwitchCompat customSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        intent = getIntent();
        intentResponse = intent.getStringExtra("userdata");

        init();

        setData();

    }

    private void setData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/driverProfileDetails.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                try {

                    JSONObject obj = new JSONObject(response);
                    JSONArray array = obj.getJSONArray("data");
                    JSONObject data = array.getJSONObject(0);

                    String name = data.getString("name");
                    String vehicle_no = data.getString("vehicle_no");
                    String phn = data.getString("phone");
                    String vehicle_type = data.getString("vehicle_type");
                    String vehicle_model = data.getString("vehicle_model");
                    String driver_image = data.getString("driver_image");
                    String driver_status = data.getString("driver_status");

                    if(driver_status.equals("Active"))
                        customSwitch.setChecked(true);
                    else
                        customSwitch.setChecked(false);

                    Glide.with(getApplicationContext())
                            .load("http://"+ip+ "/cab_booking/API/company/tbl_driver/uploads/" +driver_image)
                            .circleCrop()
                            .into(userIV);

                    driverNameTV.setText(name);
                    driverMobileTV.setText(phn);
                    driverVehicleNoTV.setText(vehicle_no);
                    driverVehicleModelTV.setText(vehicle_model);
                    driverVehicleTypeTV.setText(vehicle_type);

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
                params.put("uid",uid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void init() {

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.RetriveIP();
        uid = globalPreference.RetriveUID();

        driverNameTV = findViewById(R.id.driverNameTextView);
        driverVehicleNoTV = findViewById(R.id.driverVehicleNoTextView);
        driverVehicleModelTV = findViewById(R.id.driverVehicleModelTextView);
        driverMobileTV = findViewById(R.id.userPhoneNumberTextView);
        driverVehicleTypeTV = findViewById(R.id.driverVehicleTypeTextView);

        userIV = findViewById(R.id.userImageView);

        customSwitch = (SwitchCompat) findViewById(R.id.customSwitch);
        customSwitch.setChecked(true);
        customSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    changeDriverStatus("Active");
                else
                    changeDriverStatus("Inactive");
            }
        });


    }

    private void changeDriverStatus(String status) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+ "/cab_booking/API/driverStatusUpdate.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ProfileActivity.this, ""+response, Toast.LENGTH_SHORT).show();
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
                params.put("uid",uid);
                params.put("status",status);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}