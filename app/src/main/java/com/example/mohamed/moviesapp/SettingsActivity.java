package com.example.mohamed.moviesapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {
    RadioButton ascRadio ;
    RadioButton descRadio ;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ascRadio = findViewById(R.id.ascRadio);
        descRadio = findViewById(R.id.descRadio);
        sharedPref = new SharedPref(getApplicationContext());

        if (sharedPref.getAsc()){
            ascRadio.setChecked(true);
        }else{
            descRadio.setChecked(true);
        }

    }

    public void Save (View view){


        if (ascRadio.isChecked()){
            sharedPref.setAsc(true);
        }else{
            sharedPref.setAsc(false);
        }


        finish();
    }
}