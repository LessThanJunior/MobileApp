package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText edit_text;
    ImageButton image_button;
    TextView text_view;
    String base_url_current = "https://api.openweathermap.org/data/2.5/weather?q="; //Current weather data
    String url_current = "";
    String base_url_hourly = "https://api.openweathermap.org/data/2.5/forecast?q="; //every 3 hour weather data for 5 days
    String url_hourly = "";
    String appid = "af9814fdc4609639a202b3c81a7b5d0f";
    double temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String fName = intent.getStringExtra(MapsActivity.EXTRA_CITY);
        edit_text = (EditText)findViewById(R.id.textInputEditText);
        edit_text.setText(fName, TextView.BufferType.EDITABLE);
        image_button = (ImageButton)findViewById(R.id.imageButton);
        text_view = (TextView)findViewById(R.id.textView);
        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url_current = base_url_current + edit_text.getText().toString() + "&appid=" + appid + "&units=metric";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url_current, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                            temp = jsonObjectMain.getDouble("temp");
                            JSONArray jsonWeatherArray = jsonResponse.getJSONArray("weather");
                            JSONObject jsonWeatherObject = jsonWeatherArray.getJSONObject(0);
                            String weather_info = jsonWeatherObject.getString("main");
                            text_view.setText("Temperature: " + temp + "°С, " + weather_info);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

            }
        });
    }
}