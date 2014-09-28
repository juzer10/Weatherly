package com.weatherly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);

        TextView city = (TextView) findViewById(R.id.city);
        TextView temp = (TextView) findViewById(R.id.temp);
        Button forecast = (Button) findViewById(R.id.forecast);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        bestProvider = lm.getBestProvider(criteria, false);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //lm.requestLocationUpdates(bestProvider, 100, 1, this);

        if (location != null) {
            longitude = location.getLongitude();
            longitude = Math.floor(longitude*1000+0.5)/1000;
            latitude = location.getLatitude();
            latitude = Math.floor(latitude*1000+0.5)/1000;
            Toast.makeText(this, ""+longitude, Toast.LENGTH_SHORT).show();
        } else {
            // leads to the settings because there is no last known location
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        //  mLocationClient = new LocationClient(this, this, this);
        //  mCurrentLocation = mLocationClient.getLastLocation();
        getTemperature(latitude, longitude);
       calculateTemp();
     //   Toast.makeText(this, arr, Toast.LENGTH_SHORT).show();
       // String[] t = arr.split("|");
        //t[0] = ""+(Double.parseDouble(t[0])-273);
        temp.setText(weather.getTemp());
        city.setText(weather.getDesc());
        //Toast.makeText(this, sb, Toast.LENGTH_LONG).show();
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
           // Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
           // Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void calculateTemp() {
        String desc=null;
        try {
            JSONObject js = new JSONObject(sb.toString());
            Toast.makeText(this, ""+js, Toast.LENGTH_SHORT).show();
            JSONObject main  = js.getJSONObject("main");
            Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
            JSONArray weather1  = js.getJSONArray("weather");
            for(int i=0;i<weather1.length();i++)
            {
                JSONObject jsonobject = weather1.getJSONObject(i);
                weather.setDesc(jsonobject.getString("description"));
            }
            String t = ""+Math.round((Double.parseDouble(main.getString("temp")))-273);
            weather.setTemp(t);
            //temp +="|"+ weather.getString("description");
           // Toast.makeText(this, ""+temp+"|||"+desc, Toast.LENGTH_SHORT).show();

        }
        catch (JSONException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    public void getForecast() {
        Intent i = new Intent(this, Forecast.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
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
}
class Weather {
    String temp;
    String desc;
    Weather() {
        temp = null;
        desc = null;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTemp() {
        return temp;
    }

    public String getDesc() {
        return desc;
    }
}
