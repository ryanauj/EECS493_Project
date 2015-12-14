package otk.test;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Permission;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private GoogleApiClient ourClient;
    private Location prevLocation;
    public String LocProvider;
    public Location curLocation;
    public GoogleApiClient mGoogleApiClient;
    public LocationManager mLocationManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);
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
        // Add a marker in Sydney and move the camera

        try {
            prevLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            Log.e("security", e.getMessage());
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
        if (prevLocation != null) {
            LatLng cur_loc = new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(cur_loc).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cur_loc));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            mMap.setMyLocationEnabled(true);
        }


    }

    @Override
    public void onMapClick(LatLng point) {
    }

    public void GetMyLoc() {
        if (LocProvider != null) {
            try {
                prevLocation = mLocationManager.getLastKnownLocation(LocProvider);
            }
            catch (SecurityException e) {
                Log.e("Security",e.getMessage());
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }
            if (prevLocation != null) {
                LatLng pos = new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            }
        }
    }

    public LatLng returnMyLoc() {
        /*
        try {
            Location location = mLocationManager.getLastKnownLocation(LocProvider);

            return new LatLng(location.getLatitude(),location.getLongitude());
        }
        catch (SecurityException e) {
            Log.e("Security",e.getMessage());
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
        */
        return new LatLng(prevLocation.getLatitude(),prevLocation.getLongitude());
    }


    public boolean updateLastLoc(Location newLoc)
    {
        prevLocation = newLoc;
        if(prevLocation != null)
        {
            return true;
        }
        return false;
    }

    public void moveCamToLocation(LatLng loc) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }

    public void moveCamToLocationSmooth(LatLng loc) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(loc));
    }
}
