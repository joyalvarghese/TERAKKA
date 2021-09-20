package com.aumento.onlinetruckbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aumento.onlinetruckbooking.adapter.SearchResultAdapter;
import com.aumento.onlinetruckbooking.model.SearchResultModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DestinationActivity extends AppCompatActivity {

    private ArrayList<SearchResultModel> listModels;
    private RecyclerView recyclerView;
    private SearchResultAdapter searchResultAdapter;

    private Integer image[] = {R.drawable.home,R.drawable.timer_icon,R.drawable.timer_icon,R.drawable.timer_icon,};
    private String title[] = {"Home","Upton St. 99","Sparkvill Ave 111","James Cameron Plasa"};

    private EditText destinationET, originET;
    private LinearLayout setMarkerLocationLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("latitude",0);
        double lon = intent.getDoubleExtra("longitude",0);

        recyclerView = findViewById(R.id.rvSearchResult);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DestinationActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        listModels = new ArrayList<>();

        for (int i = 0; i < title.length; i++) {
            SearchResultModel ab = new SearchResultModel(image[i],title[i]);
            listModels.add(ab);
        }
        searchResultAdapter = new SearchResultAdapter(DestinationActivity.this, listModels);
        recyclerView.setAdapter(searchResultAdapter);

        destinationET = (EditText) findViewById(R.id.destinationEditText);
        originET = (EditText) findViewById(R.id.originEditText);

        originET.setText(getAddress(lat,lon));
        
        setMarkerLocationLL = (LinearLayout) findViewById(R.id.setMarkerLocationLinearLayout);
        setMarkerLocationLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DestinationActivity.this,DestinationMarkerActivity.class);
                intent.putExtra("latitude",lat);
                intent.putExtra("longitude",lon);
                startActivity(intent);
            }
        });
    }

    public String getAddress(double lat, double lng) {

        Log.v("IGA", "Address" + lat + "  " + lng);

        Geocoder geocoder = new Geocoder(DestinationActivity.this, Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);

            String[] splited = add.split(",");
            address = splited[0] + ", " + splited[1] + "\n" + splited[2] + ", " + splited[3] + ", " + splited[4];

            Log.v("IGA", "Address" + add);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return address;
    }

}