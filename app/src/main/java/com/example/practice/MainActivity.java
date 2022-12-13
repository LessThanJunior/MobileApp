package com.example.practice;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity{
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleBtn;
    EditText edit_text;
    ImageButton image_button;
    TextView text_view;
    String base_url_current = "https://api.openweathermap.org/data/2.5/weather?q="; //Current weather data
    String url_current = "";
    String base_url_hourly = "https://api.openweathermap.org/data/2.5/forecast?q="; //every 3 hour weather data for 5 days
    String url_hourly = "";
    String appid = "af9814fdc4609639a202b3c81a7b5d0f";
    double temp;
    CallbackManager callbackManager;
    ImageView fb_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(MainActivity.this,gso);
        AppEventsLogger.activateApp(getApplication());
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String fName = intent.getStringExtra(MapsActivity.EXTRA_CITY);
        edit_text = (EditText)findViewById(R.id.textInputEditText);
        edit_text.setText(fName, TextView.BufferType.EDITABLE);
        image_button = (ImageButton)findViewById(R.id.imageButton);
        text_view = (TextView)findViewById(R.id.textView);
        googleBtn = (ImageView) findViewById(R.id.google_btn);
        fb_login  = findViewById(R.id.fb_btn);
        callbackManager = CallbackManager.Factory.create();


        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn){
            Intent fb_intent = new Intent(MainActivity.this,FacebookSign.class);
            startActivity(fb_intent);
            finish();

        }


        fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));
            }
        });



        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.i("Log","Success");
                        Intent intent = new Intent(MainActivity.this,FacebookSign.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.i("Log","Cancel");
                        Toast.makeText(getApplicationContext(),"Facebook login cancel",Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.i("Log","Error"+exception.toString());
                        Toast.makeText(getApplicationContext(),"Facebook login error",Toast.LENGTH_LONG);
                    }
                });


        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null){
            navigateToSecondActivity();
        }


        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url_current = base_url_current + edit_text.getText().toString() + "&appid=" + appid + "&units=metric";
                url_hourly = base_url_hourly + edit_text.getText().toString() + "&appid=" + appid + "&units=metric";
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
                StringRequest hourlyRequest = new StringRequest(Request.Method.GET, url_hourly, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<ItemWeather> weatherList = new ArrayList<>();
                        try {
                            JSONObject jsonResponseHourly = new JSONObject(response);
                            JSONArray getWeatherList = jsonResponseHourly.getJSONArray("list");
                            for (int i = 0; i < 40; i++) {
                                JSONObject getWeather = getWeatherList.getJSONObject(i);
                                JSONObject getMain = getWeather.getJSONObject("main");
                                System.out.println(getMain);
                                double temp_hourly = getMain.getDouble("temp");
                                String time = getWeather.getString("dt_txt");
                                ItemWeather weather = new ItemWeather();
                                weather.setTemp(temp_hourly);
                                weather.setTime(time);
                                weatherList.add(weather);
                            }
                            ItemWeatherRecyclerView weatherRecyclerView = new ItemWeatherRecyclerView(weatherList);
                            RecyclerView recyclerView = findViewById(R.id.rvWeather);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(weatherRecyclerView);
                        } catch(JSONException e) {e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
                requestQueue.add(hourlyRequest);
            }
        });
        ImageButton googleMapButton = (ImageButton)findViewById(R.id.googleMapButton);
        googleMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });
    }
    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }

    }
    void navigateToSecondActivity(){
        finish();
        Intent intent = new Intent(MainActivity.this,GoogleSign.class);
        startActivity(intent);
    }

}
