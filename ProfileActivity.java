package com.aumento.onlinetruckbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.aumento.onlinetruckbooking.Utils.GlobalPreference;
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

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;

    EditText userNameET,userLNameET,userEmailET;
    TextView userMobileTV,submitButtonTV,changePasswordButtonTV;

    private Intent intent;
    String intentResponse;
    GlobalPreference globalPreference;
    private String ip;
    private String uid;
    private String image = "";

    boolean IMAGE_CHANGE = false;
    private ImageView userIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        intent = getIntent();
        intentResponse = intent.getStringExtra("userdata");

        init();

        setData();

        submitButtonTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

    }

    private void updateData() {

        if(IMAGE_CHANGE) {
            image = getStringImage(bitmap);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+ "/cab_booking/API/userProfileUpdate.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ProfileActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                /*Log.d(TAG, "onResponse: "+response);
                Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                startActivity(intent);*/
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
                params.put("fname",userNameET.getText().toString());
                params.put("lname",userLNameET.getText().toString());
                params.put("email",userEmailET.getText().toString());
                params.put("uid",uid);
                if(IMAGE_CHANGE)
                    params.put("image",image);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                Glide.with(getApplicationContext())
                        .load(bitmapToByte(bitmap))
                        .circleCrop()
                        .into(userIV);
                userIV.setImageBitmap(bitmap);
                IMAGE_CHANGE = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void setData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/cab_booking/API/userProfileDetails.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                try {

                    JSONObject obj = new JSONObject(response);
                    JSONArray array = obj.getJSONArray("data");
                    JSONObject data = array.getJSONObject(0);

                    String name = data.getString("first_name");
                    String lname = data.getString("last_name");
                    String phn = data.getString("phone_number");
                    String email = data.getString("email_id");

                    String image = data.getString("user_image");

                    Log.d(TAG, "User Image :"+image);

                    Glide.with(getApplicationContext())
                            .load("http://"+ip+ "/cab_booking/API/admin/tbl_user/uploads/" +image)
                            .circleCrop()
                            .into(userIV);

                    userNameET.setText(name);
                    userLNameET.setText(lname);
                    userMobileTV.setText(phn);
                    userEmailET.setText(email);

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

        userNameET = findViewById(R.id.userFirstNameTextView);
        userLNameET = findViewById(R.id.UserLastNameTextView);
        userEmailET = findViewById(R.id.userEmailTextView);
        userMobileTV = findViewById(R.id.userPhoneNumberTextView);
        submitButtonTV = findViewById(R.id.submitButtonTextView);

        userIV = findViewById(R.id.userImageView);
        userIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

    }
}