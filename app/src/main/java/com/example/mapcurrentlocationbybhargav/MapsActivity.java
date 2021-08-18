package com.example.mapcurrentlocationbybhargav;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mapcurrentlocationbybhargav.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final int LOCATION_REQUEST= 500;

    ArrayList<LatLng> listPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)
                this.getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        listPoints =new ArrayList<>();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*// Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(latLng -> {
            //set marks when already 2
            if (listPoints.size() == 2)
            {
                listPoints.clear();
                mMap.clear();
            }//save fist point
            listPoints.add(latLng);
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(latLng);
            if (listPoints.size() == 1)
            {
                // mark 1 add
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

            }else
            {
                // second ,arks
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            }
            mMap.addMarker(markerOptions);

            if (listPoints.size()>=2)
            {

                // આ કોદે તે કરેલો છે તુ કિક સોલુતિઓન ગોતોતો એત્લે aa joje



               LatLng origin = (LatLng) listPoints.get(0);
                LatLng dest = (LatLng) listPoints.get(1);

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
                //url requeast mark 1 to 2
                String url1=getRequeastUrl(listPoints.get(0),listPoints.get(1));
                Toast.makeText(MapsActivity.this, url1, Toast.LENGTH_LONG).show();

                TaskRequestDirection taskRequestDirection=new TaskRequestDirection();
                taskRequestDirection.execute(url1);
            }

        });

    }

    private String getRequeastUrl(LatLng origin, LatLng dest) {
        //value of origin
        String str_org="origin="+origin.latitude+","+origin.longitude;
        //Sting of dest
        String str_dest="destination="+dest.latitude+","+dest.longitude;
        //set value enable the sensor
        String sensor ="sensor=false";
        //mode for direction
        String mode="mode=driving";
        // Build full param
        String param=str_org+"&"+str_dest+"&"+sensor+"&"+mode;
        // output format
        String output="json";
        //create url request
        //String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param +"&key=" +"AIzaSyCJj3b1y1WBYwr88NOzEmLhHB9Tbq34oik";
        String url= "https://maps.googleapis.com/maps/api/directions/" + output + "?" +param;
        return  url;
    }
 private String requestDirection(String reqUrl) throws IOException {
    String responseString="";
     InputStream inputStream=null;
     HttpURLConnection httpURLConnection=null;
     try {
         URL url=new URL(reqUrl);

         httpURLConnection = (HttpURLConnection) url.openConnection();
         httpURLConnection.connect();

         // get the response results
         inputStream = httpURLConnection.getInputStream();
         InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
         BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

         StringBuffer stringBuffer=new StringBuffer();
         String line="";
         while((line=bufferedReader.readLine()) !=null)
         {
             stringBuffer.append(line);
         }
         responseString=stringBuffer.toString();
         bufferedReader.close();
         inputStreamReader.close();

     }catch (Exception e)
     {
         e.printStackTrace();
        // Toast.makeText(MapsActivity.this, e.toString()+"1", Toast.LENGTH_LONG).show();
     }finally {
         if (inputStream !=null)
         {
             inputStream.close();
         }
         httpURLConnection.disconnect();
     }
    return  responseString;
 }


    @SuppressLint({"MissingPermission"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }
    public class TaskRequestDirection extends AsyncTask<String, Void,String>
    {

        @Override
        protected String doInBackground(String... strings) {
         String responseString= "";
            //Toast.makeText(MapsActivity.this, responseString+"data", Toast.LENGTH_SHORT).show();
        try {
            responseString=requestDirection(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(MapsActivity.this, e.toString()+"2", Toast.LENGTH_LONG).show();
        }
        return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //json here parse
            TaskParser taskParser=new TaskParser();
            taskParser.execute(s);

        }
    }
    public class TaskParser extends AsyncTask<String,Void,List<List<HashMap<String, String>>> >
    {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject=null;
            List<List<HashMap<String, String>>> routes=null;
            try {
                jsonObject =new JSONObject(strings[0]);

                DirectionsParser directionsParser=new DirectionsParser();
                routes=directionsParser.parse(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
               // Toast.makeText(MapsActivity.this, e.toString()+"3 ", Toast.LENGTH_LONG).show();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {

            // get list aND DISPLAY it into the map
            ArrayList<LatLng> points= new ArrayList<>();
            PolylineOptions polylineOptions= null;

            for (List<HashMap<String, String>> path : lists)
            {
                points=new ArrayList<>();
                //Toast.makeText(MapsActivity.this, path.toString(), Toast.LENGTH_LONG).show();

                polylineOptions =new PolylineOptions();
                for (HashMap<String, String> point : path)
                {
                    double lat=Double.parseDouble(point.get("lat"));
                    double lon=Double.parseDouble(point.get("lon"));


                    LatLng position = new LatLng(lat,lon);
                    points.add(position);
                    Toast.makeText(MapsActivity.this, points.toString(), Toast.LENGTH_LONG).show();
                }
                polylineOptions.addAll(points);
                polylineOptions.width(8);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);

            }
            if(polylineOptions !=null)
            {
                mMap.addPolyline(polylineOptions);
            }else
                {
                    Toast.makeText(MapsActivity.this, "Direction not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}