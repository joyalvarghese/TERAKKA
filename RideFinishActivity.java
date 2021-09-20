package com.aumento.onlinetruckbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RideFinishActivity extends AppCompatActivity {

    TextView priceTextView, timeTextView, nameTextView, doneTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_finish);

        init();

        Intent intent = getIntent();
        String response = intent.getStringExtra("response");

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            JSONObject object = jsonArray.getJSONObject(0);
            String name = object.getString("name");
            String price = object.getString("price");
            String date_time = object.getString("date_time");

            priceTextView.setText("₹"+price);
            nameTextView.setText(name);
            timeTextView.setText(date_time);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        priceTextView = (TextView) findViewById(R.id.priceTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        doneTextView = (TextView) findViewById(R.id.doneTextView);

        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RideFinishActivity.this, UserHomeMapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}