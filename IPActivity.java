package com.aumento.onlinetruckbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aumento.onlinetruckbooking.Utils.GlobalPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class IPActivity extends AppCompatActivity {

    private static final String TAG = "IPActivity";

    private GlobalPreference mGlobalPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);

        mGlobalPreference= new GlobalPreference(getApplicationContext());
        getIP();

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 0);

    }

    public void getIP() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("    Enter Your IP Address    ");

        final EditText input = new EditText(IPActivity.this);
        input.setText(mGlobalPreference.RetriveIP());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Text entered is " + input.getText().toString(), Toast.LENGTH_SHORT).show();

                mGlobalPreference.addIP(input.getText().toString());

                input.setText(input.getText().toString());
                Log.d(TAG, "onClick: "+mGlobalPreference.getLoginStatus());
                if(mGlobalPreference.getLoginStatus())
                    check();
                else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }

            }
        });

        builder.show();

    }

    private void check() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+mGlobalPreference.RetriveIP()+"/cab_booking/API/checkRideStatus.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                if(response.equals("failed"))
                {
                    startActivity(new Intent(getApplicationContext(), UserHomeMapsActivity.class));
                    finish();
                }
                else
                {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject object = jsonArray.getJSONObject(i);
                            double start_lat = object.getDouble("start_lat");
                            double start_lon = object.getDouble("start_lon");
                            double dest_lat = object.getDouble("dest_lat");
                            double dest_lon = object.getDouble("dest_lon");
                            String did = object.getString("did");
                            String rid = object.getString("id");

                            Intent intent = new Intent(IPActivity.this,RideStartedActivity.class);
                            intent.putExtra("olat",start_lat);
                            intent.putExtra("olon",start_lon);
                            intent.putExtra("dlat",dest_lat);
                            intent.putExtra("dlon",dest_lon);
                            intent.putExtra("did",did);
                            intent.putExtra("rid",rid);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("uid",mGlobalPreference.RetriveUID());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


}