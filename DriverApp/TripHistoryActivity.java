package com.aumento.onlinecabdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aumento.onlinecabdriver.Adapter.TripHistoryRecycleAdapter;
import com.aumento.onlinecabdriver.ModelClass.TripHistoryModelClass;
import com.aumento.onlinecabdriver.Utils.GlobalPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripHistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";

    private RecyclerView tripHistoryRV;
    private GlobalPreference globalPreference;
    private String ip;
    private ArrayList<TripHistoryModelClass> tripHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.RetriveIP();

        tripHistoryRV = (RecyclerView) findViewById(R.id.tripHistoryRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        tripHistoryRV.setLayoutManager(layoutManager);

        getTripHistory();
    }

    private void getTripHistory() {

        tripHistoryList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/driverTripHistory.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: Trip History" + response);

                if (response.equals("failed")) {

                    Toast.makeText(TripHistoryActivity.this, "Please try Agin", Toast.LENGTH_SHORT).show();

                } else {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("da");
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String id = object.getString("id");
                            String start_location = object.getString("start_location");
                            String dest_location = object.getString("dest_location");
                            String start_lat = object.getString("start_lat");
                            String start_lon = object.getString("start_lon");
                            String dest_lat = object.getString("dest_lat");
                            String dest_lon = object.getString("dest_lon");
                            String trip_amount = object.getString("trip_amount");
                            String trip_date = object.getString("trip_date");
                            String start_time = object.getString("start_time");
                            String end_time = object.getString("end_time");
                            String payment = object.getString("payment");
                            String vehicle_type = object.getString("vehicle_type");
                            String name = object.getString("first_name")+" "+object.getString("last_name");

                            tripHistoryList.add(new TripHistoryModelClass(id,name,start_location,dest_location,start_lat,start_lon,
                                    dest_lat,dest_lon,trip_date,trip_amount,start_time,end_time,payment,vehicle_type));
                        }

                        TripHistoryRecycleAdapter adapter = new TripHistoryRecycleAdapter(TripHistoryActivity.this,tripHistoryList);
                        tripHistoryRV.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                params.put("uid",globalPreference.RetriveUID());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


}