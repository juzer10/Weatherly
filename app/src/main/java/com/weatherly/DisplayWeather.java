package com.weatherly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DisplayWeather extends Activity implements LocationListener {
    Weather weather;
    double latitude;
    double longitude;
    String bestProvider;
    public StringBuilder sb = new StringBuilder();
    LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        weather = new Weather();
        setContentView(R.layout.display_weather);
        super.onCreate(savedInstanceState);

        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.full);
        TextView city = (TextView) findViewById(R.id.city);
        TextView temp = (TextView) findViewById(R.id.temp);
        Button forecast = (Button) findViewById(R.id.forecast);

        getLocation(weather);
        getTemperature(weather.getLatitude(), weather.getLongitude());
        calculateTemp();

        //Change background color based on Current Temperature
        double currentTemp = Double.parseDouble(weather.getTemp());
        if(currentTemp < 0)
            mLinearLayout.setBackgroundColor(getResources().getColor(R.color.cyan));
        else if(currentTemp >= 0 && currentTemp <= 20)
            mLinearLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        else if(currentTemp > 20 && currentTemp <=30)
            mLinearLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
        else
            mLinearLayout.setBackgroundColor(Color.RED);
        temp.setText(weather.getTemp()+" °C");
        city.setText(weather.getDesc());
    }

    public void getTemperature(double lat, double longi) {
        try {
            URL u = new URL("http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+longi);
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


    public void calculateTemp() {
        try {
            JSONObject js = new JSONObject(sb.toString());
            JSONObject main  = js.getJSONObject("main");
            JSONArray weather1  = js.getJSONArray("weather");
            for(int i=0;i<weather1.length();i++)
            {
                JSONObject jsonobject = weather1.getJSONObject(i);
                weather.setDesc(jsonobject.getString("description"));
            }
            String t = ""+Math.round((Double.parseDouble(main.getString("temp")))-273);
            weather.setTemp(t);

        }
        catch (JSONException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    public void getForecast(View view) {
        Intent i = new Intent(this, Forecast.class);
        i.putExtra("lat", ""+latitude);
        i.putExtra("lon", ""+longitude);
        startActivity(i);
        overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {

        super.onResume();
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1, this);
    }

    protected void onPause() {
        super.onPause();
        lm.removeUpdates(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    public void getLocation(Weather weather) {
        StrictMode.ThreadPolicy policy = new StrictMode.
        ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        bestProvider = lm.getBestProvider(criteria, false);
        //Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location location = lm.getLastKnownLocation(bestProvider);
        if(location == null)
        {
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //If FPS cannot lock your location
        }
        if (location != null) {
            longitude = location.getLongitude();
            weather.setLongitude(Math.floor(longitude * 1000 + 0.5) / 1000); //Round off longitude and latitude for api call
            latitude = location.getLatitude();
            weather.setLatitude(Math.floor(latitude * 1000 + 0.5) / 1000);

        } else {
            // leads to the settings because there is no last known location
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }
}
class Weather {
    String temp;
    String desc;
    double latitude;
    double longitude;
    Weather() {
        latitude = 0.0;
        longitude = 0.0;
        temp = null;
        desc = null;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTemp() {
        return temp;
    }

    public String getDesc() {
        return desc;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
