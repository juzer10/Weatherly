package com.weatherly;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Created by juzer_000 on 9/27/2014.
 */
public class ShowWeather extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditText weather = (EditText)findViewById(R.id.weather);
        String city = weather.getText().toString();


    }
}
