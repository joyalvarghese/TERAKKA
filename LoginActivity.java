package com.aumento.onlinetruckbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.aumento.onlinetruckbooking.adapter.SelectCountryCodeRecycleAdapter;
import com.aumento.onlinetruckbooking.model.SelectCountryCodeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    TextView nextButtonTV;
    EditText phoneNumberET;
    private ArrayList<SelectCountryCodeModel> selectCountryCodeModels;
    private RecyclerView recyclerView;
    private SelectCountryCodeRecycleAdapter bAdapter;

    private String country_code[] = {"+91","+1","+52","+61","+91","+43","+91","+1","+52","+61","+91","+43"};

    Dialog slideDialog;
    LinearLayout sppiner;
    ImageView img;
    TextView txt;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        GlobalPreference globalPreference = new GlobalPreference(this);
        ip = globalPreference.RetriveIP();
        txt = findViewById(R.id.country_code);

        /*bottom dialog code is here*/

        sppiner = findViewById(R.id.sppiner);
        sppiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideDialog = new Dialog(LoginActivity.this, R.style.CustomDialogAnimation);
                slideDialog.setContentView(R.layout.select_country_popup);


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
                layoutParams.height = height;
                layoutParams.gravity = Gravity.BOTTOM;


                recyclerView = slideDialog.findViewById(R.id.recyclerview);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LoginActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                selectCountryCodeModels = new ArrayList<>();

                for (int i = 0; i < country_code.length; i++) {
                    SelectCountryCodeModel mycreditList = new SelectCountryCodeModel(country_code[i]);
                    selectCountryCodeModels.add(mycreditList);
                }
                bAdapter = new SelectCountryCodeRecycleAdapter(LoginActivity.this,selectCountryCodeModels);
                recyclerView.setAdapter(bAdapter);

                slideDialog.getWindow().setAttributes(layoutParams);
                slideDialog.setCancelable(true);
                slideDialog.setCanceledOnTouchOutside(true);
                slideDialog.show();
            }
        });
    }

    private void init() {

        phoneNumberET = (EditText) findViewById(R.id.phoneNumberEditText);

        nextButtonTV = (TextView) findViewById(R.id.nextButtonTV);
        nextButtonTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPhone();
            }
        });

    }

    private void registerPhone() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/userPhoneRegister.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                if(!response.equals("failed")) {

                    Intent intent = new Intent(LoginActivity.this,OtpActivity.class);
                    intent.putExtra("response",response);
                    startActivity(intent);
                    finish();

                }
                else{
                    Toast.makeText(LoginActivity.this, ""+response, Toast.LENGTH_SHORT).show();
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
                params.put("phone_number",txt.getText().toString()+phoneNumberET.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void selectedCountry(String country_code) {
        txt.setText(country_code);
        slideDialog.dismiss();
    }
}