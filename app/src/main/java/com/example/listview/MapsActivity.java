package com.example.listview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng tempLatLng = new LatLng(0,0);
    private double latitudeLoc,longitudeLoc;

    private Intent i;
    private String state;
    private String location;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        i = getIntent();
        state = i.getStringExtra("state");
        location = i.getStringExtra("location");
        address = i.getStringExtra("address");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menubar_map, menu);

        return true;
    }

    // should have page that call this method
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuDone) {
            // press done will go that activity
            Intent returnIntent = new Intent();
            if (latitudeLoc != 0 && longitudeLoc != 0) {
                returnIntent.putExtra("location",latitudeLoc+","+longitudeLoc);
                returnIntent.putExtra("address",getAddress(latitudeLoc,longitudeLoc));
//                Log.i("kong","Address : " + getAddress(latitudeLoc,longitudeLoc));
                setResult(RESULT_OK, returnIntent);
            }
            finish();
        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(13.84,100.43)));

//        if("direction".equals(state)){
        if(location != null && address != null){
            String[] locLag = location.split(",");
            MarkerOptions marker = new MarkerOptions();
            marker.position(new LatLng(Double.parseDouble(locLag[0]),Double.parseDouble(locLag[1])));
            mMap.addMarker(marker);
            marker.title("location");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.parseDouble(locLag[0]),Double.parseDouble(locLag[1])), 16));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else if(!"direction".equals(state)){
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (latLng!=tempLatLng){
                        mMap.clear();
                    }
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    latitudeLoc = latLng.latitude;
                    longitudeLoc = latLng.longitude;
                    tempLatLng = latLng;
                }
            });
        }
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                if (address.getAddressLine(0) != null) {
                    result.append(address.getAddressLine(0)).append(" ");
                }
                if (address.getLocality() != null) {
                    result.append(address.getLocality()).append(" ");
                }
                if (address.getAdminArea() != null) {
                    result.append(address.getAdminArea()).append(" ");
                }
                if (address.getCountryName() != null) {
                    result.append(address.getCountryName()).append(" ");
                }
                if (address.getCountryCode() != null) {
                    result.append(address.getCountryCode()).append(" ");
                }
                if (address.getPostalCode() != null) {
                    result.append(address.getPostalCode()).append(" ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}