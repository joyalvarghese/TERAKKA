package com.aumento.onlinetruckbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class UserDetailsRegisterActivity extends AppCompatActivity {

    private static final String TAG = "UserDetailsRegisterActi";

    private EditText firstNameET,lastNameET,emailET;
    private TextView getStartedButtonTV;
    private String ip;
    private String user_id;
    private GlobalPreference globalPreference;
    private String response;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details_register);

        init();

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.RetriveIP();
        user_id = globalPreference.RetriveUID();

        Log.d(TAG, "onCreate: "+user_id);

        getStartedButtonTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitUserDatas();
            }
        });
    }

    private void submitUserDatas() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/userDetailsUpdate.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                if(!response.equals("failed"))
                {

                    globalPreference.setLoginStatus(true);

                    Intent intent = new Intent(UserDetailsRegisterActivity.this,UserHomeMapsActivity.class);
                    intent.putExtra("response",response);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(UserDetailsRegisterActivity.this, ""+response, Toast.LENGTH_SHORT).show();
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
                params.put("user_id",user_id);
                params.put("first_name",firstNameET.getText().toString());
                params.put("last_name",lastNameET.getText().toString());
                params.put("email",emailET.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void init() {

        getStartedButtonTV = (TextView) findViewById(R.id.getStartedButtonTV);
        firstNameET = (EditText) findViewById(R.id.firstNameEditText);
        lastNameET = (EditText) findViewById(R.id.lastNameEditText);
        emailET = (EditText) findViewById(R.id.emailEditText);


    }
}