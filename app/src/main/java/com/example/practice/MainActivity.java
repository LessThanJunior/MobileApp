package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    EditText edit_text;
    ImageButton image_button;
    TextView text_view;
    double temp = 10;
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
                text_view.setText("Temperature: " + temp + "° C");
            }
        });
    }
}