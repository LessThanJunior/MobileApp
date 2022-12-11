package com.example.practice;

import androidx.fragment.app.FragmentActivity;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.practice.databinding.ActivityMapsBinding;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    public static final String EXTRA_CITY = "com.example.application.example.EXTRA_CITY";
    public String base_url = "https://api.openweathermap.org/geo/1.0/reverse"; //Geomapping
    public String base_url2 = "https://api.openweathermap.org/data/2.5/weather"; //Current weather data
    public String base_url3 = "https://api.openweathermap.org/data/2.5/forecast"; //every 3 hour weather data for 5 days
    public String url = ""; //Requested url
    public String appid = "af9814fdc4609639a202b3c81a7b5d0f";
    String mAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("New Marker");
                googleMap.addMarker(marker);
                url = base_url + "?lat=" + point.latitude + "&lon=" + point.longitude + "&limit=1&appid="+appid;
                System.out.println(url);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonResponse = new JSONArray(response);
                            JSONObject jsonObject = jsonResponse.getJSONObject(0);
                            JSONObject jsonObjectLang = jsonObject.getJSONObject("local_names");
                            mAddress = jsonObjectLang.getString("en");
                            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                            intent.putExtra(EXTRA_CITY, mAddress);
                            startActivity(intent);
                            System.out.println(mAddress);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapsActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

            };

        });
    }
};