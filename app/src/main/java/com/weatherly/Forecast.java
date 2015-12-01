package com.weatherly;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
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
import java.util.Calendar;

/**
 * Created by juzer_000 on 9/27/2014.
 */
public class Forecast extends Activity {
    double latitude;
    double longitude;
    public StringBuilder sb = new StringBuilder();
    Weather weather[];
    String appid = "cc51abe0a1cc9590aed22875695b2d87";

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

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent in = getIntent();
        latitude = Double.parseDouble(in.getStringExtra("lat"));
        longitude = Double.parseDouble(in.getStringExtra("lon"));

        getForecast(latitude, longitude);
        calculateForecast();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);


        forecast1.setText(getDay(day+1)+" - "+weather[0].getTemp()+"°C - "+weather[0].getDesc());
        forecast2.setText(getDay(day+2)+" - "+weather[1].getTemp()+"°C - "+weather[1].getDesc());
        forecast3.setText(getDay(day+3)+" - "+weather[2].getTemp()+"°C - "+weather[2].getDesc());
        forecast4.setText(getDay(day+4)+" - "+weather[3].getTemp()+"°C - "+weather[3].getDesc());
        forecast5.setText(getDay(day+5)+" - "+weather[4].getTemp()+"°C - "+weather[4].getDesc());


        setBackground(forecast1, weather[0]);
        setBackground(forecast2, weather[1]);
        setBackground(forecast3, weather[2]);
        setBackground(forecast4, weather[3]);
        setBackground(forecast5, weather[4]);

    }

    public void setBackground(TextView tv, Weather weather) {
        double currentTemp = Double.parseDouble(weather.getTemp());
        if(currentTemp < 0)
            tv.setBackgroundColor(getResources().getColor(R.color.cyan));
        else if(currentTemp >= 0 && currentTemp <= 20)
            tv.setBackgroundColor(getResources().getColor(R.color.blue));
        else if(currentTemp > 20 && currentTemp <=30)
            tv.setBackgroundColor(getResources().getColor(R.color.yellow));
        else
            tv.setBackgroundColor(Color.RED);
    }

    public String getDay(int day) {
        String today = "";
        if(day > 7)
            day = day%7;
        switch (day) {
            case Calendar.SUNDAY:
                today = "Sun";
                break;
            case Calendar.MONDAY:
                today = "Mon";
                break;
            case Calendar.TUESDAY:
                today = "Tue";
                break;
            case Calendar.WEDNESDAY:
                today = "Wed";
                break;
            case Calendar.THURSDAY:
                today = "Thu";
                break;
            case Calendar.FRIDAY:
                today = "Fri";
                break;
            case Calendar.SATURDAY:
                today = "Sat";
                break;

        }
        return today;
    }

    public void getForecast(double lat, double longi) {
        try {
            URL u = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat="+lat+"&lon="+longi+"&appid="+appid+"&cnt=5&mode=json");
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

        } catch (IOException ex) {
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
    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        switch (menu.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                return true;
        }
        return super.onOptionsItemSelected(menu);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
    }
}
