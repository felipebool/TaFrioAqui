package br.com.sasquati.tafrioaqui;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity implements LocationListener {
    private Button taFrioBtn;

    private String latitude;
    private String longitude;

    private URL webServiceURL;

    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        taFrioBtn = (Button)findViewById(R.id.taFrioBtn);
        taFrioBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    webServiceURL = new URL("http://www.onemandev.tk/webservice/addpoint.php");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                // Creating json object
                    JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", "placeholder");
                    jsonObject.put("address", "placeholder");
                    jsonObject.put("latitude", latitude);
                    jsonObject.put("longitude", longitude);
                    jsonObject.put("type", "placeholder");
                    sendCoordinates(jsonObject, webServiceURL);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("WEBSERVICE[being sent]", jsonObject.toString());
            }
        });
    }

    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    public boolean sendCoordinates(JSONObject json, URL url) throws IOException {
        // Setting up connection
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setChunkedStreamingMode(0);

        // Sending message
        OutputStreamWriter postMessage = new OutputStreamWriter(httpURLConnection.getOutputStream());
        postMessage.write(json.toString());
        postMessage.flush();

        StringBuilder stringBuilder = new StringBuilder();

        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStreamReader streamReader = new InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(streamReader);

            String response = null;
            while ((response = bufferedReader.readLine()) != null) {
                stringBuilder.append(response + "\n");
            }
            bufferedReader.close();

            Log.d("WEBSERVICE[received]", stringBuilder.toString());
            Toast.makeText(getApplicationContext(), "Obrigado por ajudar!", Toast.LENGTH_LONG).show();
            return true;
        }
        else {
            Log.d("WEBSERVICE[error]", String.valueOf(httpURLConnection.getResponseCode()));
            return false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
