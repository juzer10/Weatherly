package com.weatherly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by juzer_000 on 9/27/2014.
 */
public class Forecast extends Activity {
    double latitude;
    double longitude;
    public StringBuilder sb = new StringBuilder();
    Weather weather[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast);
        weather = new Weather[5];
        TextView forecast1 = (TextView) findViewById(R.id.forecast1);
        TextView forecast2 = (TextView) findViewById(R.id.forecast2);
        TextView forecast3 = (TextView) findViewById(R.id.forecast3);
        TextView forecast4 = (TextView) findViewById(R.id.forecast4);
        TextView forecast5 = (TextView) findViewById(R.id.forecast5);

        Intent in = getIntent();
        latitude = Double.parseDouble(in.getStringExtra("lat"));
        longitude = Double.parseDouble(in.getStringExtra("lon"));

        getForecast(latitude, longitude);
        calculateForecast();

        forecast1.setText(weather[0].getTemp()+"°C - "+weather[0].getDesc());
        forecast2.setText(weather[1].getTemp()+"°C - "+weather[1].getDesc());
        forecast3.setText(weather[2].getTemp()+"°C - "+weather[2].getDesc());
        forecast4.setText(weather[3].getTemp()+"°C - "+weather[3].getDesc());
        forecast5.setText(weather[4].getTemp()+"°C - "+weather[4].getDesc());

    }

    public void getForecast(double lat, double longi) {
        try {
            URL u = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat="+lat+"&lon="+longi+"&cnt=5&mode=json");
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);

                    }
                    br.close();
            }


        } catch (MalformedURLException ex) {
            // Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            // Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void calculateForecast() {
        try {
            JSONObject js = new JSONObject(sb.toString());
            JSONArray list  = js.getJSONArray("list");
            for(int j=0;j<list.length();j++)
            {
                weather[j] = new Weather();
                JSONObject jsonObject = list.getJSONObject(j);
                JSONObject d = jsonObject.getJSONObject("temp");
                String t = ""+Math.round((Double.parseDouble(d.getString("day")))-273);
                weather[j].setTemp(t);
                JSONArray weather1  = jsonObject.getJSONArray("weather");
                for(int i=0;i<weather1.length();i++)
                 {
                 JSONObject jsonobject = weather1.getJSONObject(i);
                 weather[j].setDesc(jsonobject.getString("main"));
                 }
            }

        }
        catch (JSONException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }
}
