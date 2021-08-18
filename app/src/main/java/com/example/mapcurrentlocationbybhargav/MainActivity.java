package com.example.mapcurrentlocationbybhargav;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.mapcurrentlocationbybhargav.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements LocationListener {
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat, textView5;
    String lat;
    String provider;
    Button btnShowMap;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    ListView lvExpenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLat = (TextView) findViewById(R.id.textview1);
        lvExpenseList = findViewById(R.id.lvExpenseList);
        btnShowMap = findViewById(R.id.btnShowMap);

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        //textView5=findViewById(R.id.textView5);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    @Override
    public void onLocationChanged(Location location) {
            txtLat = (TextView) findViewById(R.id.textview1);
            txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
            List<String> expenseTypes = new ArrayList<>();

        Geocoder geocoder=new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses= null;
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),location.getLongitude(),1);
            /*Toast.makeText(context, Html.fromHtml(
                    "<font color='#6200EE'><b>Address :</b><br></font>"
                            +addresses.get(0).getAddressLine(0)), Toast.LENGTH_LONG).show();*/

            expenseTypes.addAll(Collections.singleton(Html.fromHtml(
                    addresses.get(0).getAddressLine(0)).toString().trim()));

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, expenseTypes);
            lvExpenseList.setAdapter(arrayAdapter);
            /*lvExpenseList.setOnItemClickListener((adapterView, view, i, l) -> {
                if (i==0){
                    startActivity(new Intent(getContext(), ExpenseDetailsScreen.class));
                }
            });*/
            //textView5.setText();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
}