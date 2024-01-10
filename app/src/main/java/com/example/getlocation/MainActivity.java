package com.example.getlocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationClient;
    LocationServices LocationServices;

    boolean getLastLocation = true;
    LocationCallback locationCallback;

    double latitude = 0.0;
    double longitude = 0.0;

    boolean isRefetch = false;

    int amountUpDateLocation = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getLocation = findViewById(R.id.button);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        Button getLocation2 = findViewById(R.id.button2);
        getLocation2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation = !getLastLocation;
                if(getLastLocation){
                    getLocation2.setText("getLastLocation");
                }else{
                    getLocation2.setText("getHeightLocation");
                }
            }
        });
    }


    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
            return;
        }

        if(isRefetch == false){
            getLastLocation();
        }

    }


    public void getHeigthLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // ตั้งค่าความแม่นยำสูงสุด
        locationRequest.setInterval(10000); // ระยะเวลาในการรอรับข้อมูลตำแหน่ง (ในมิลลิวินาที)
        locationRequest.setFastestInterval(5000);

    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // ตั้งค่าความแม่นยำสูงสุด
        locationRequest.setInterval(500); // ระยะเวลาในการรอรับข้อมูลตำแหน่ง (ในมิลลิวินาที)
        isRefetch = true;
        amountUpDateLocation = 0;
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if(location != null){
                        double latitude1 = location.getLatitude();
                        double longitude1 = location.getLongitude();

                        if(latitude != latitude1 && longitude != longitude1 && isRefetch){
                            fusedLocationClient.removeLocationUpdates(locationCallback);
                            isRefetch = false;
                            TextView _latitude, _longitude;
                            _latitude = findViewById(R.id.textView5);
                            _longitude = findViewById(R.id.textView6);

                            _latitude.setText("latitude : " + latitude1);
                            _longitude.setText("longitude : " + longitude1);
                            latitude = latitude1;
                            longitude = longitude1;

                            //ดึงชื่อ

                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses = null;

                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (addresses != null && addresses.size() > 0) {
                                String address = addresses.get(0).getAddressLine(0);
                                TextView add = findViewById(R.id.textView7);
                                add.setText("address : " + address);

                                // ทำอะไรบางอย่างกับข้อมูลที่ได้รับ
                            }
                            break;

                        }else if(amountUpDateLocation == 1){
                            fusedLocationClient.removeLocationUpdates(locationCallback);
                            isRefetch = false;
                            latitude = latitude1;
                            longitude = longitude1;
                            amountUpDateLocation = 0;
                            return;
                        }
                        else{
                            amountUpDateLocation += 1;
                        }

                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);


//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) {
//                            double latitude = location.getLatitude();
//                            double longitude = location.getLongitude();
//                            TextView _latitude, _longitude;
//                            _latitude = findViewById(R.id.textView5);
//                            _longitude = findViewById(R.id.textView6);
//
//                            _latitude.setText("latitude : " + latitude);
//                            _longitude.setText("longitude : " + longitude);
//
//
//                            // ใช้ตำแหน่ง latitude และ longitude ที่ได้รับ
//                        }
//                    }
//                });
    }

    public void test(){
        int a= 20;
    }
}