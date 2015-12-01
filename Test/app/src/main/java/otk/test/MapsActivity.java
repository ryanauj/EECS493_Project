package otk.test;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private GoogleApiClient ourClient;
    private Location prevLocation;
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(loc).title("Point My Location"));
            if(mMap != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);
        /* {
            @Override
            public void onMapLongClick(LatLng point) {

                mMap.addMarker(new MarkerOptions().position(point).title("Point new"));
            }
        });*/
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
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        sydney = new LatLng(34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker somewhere"));
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);

        //ourClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
    }

    @Override
    public void onMapClick(LatLng point)
    {
        mMap.addMarker(new MarkerOptions().position(point).title("Point new"));
    }

    @Override
    public void onMapLongClick(LatLng point)
    {
        mMap.addMarker(new MarkerOptions().position(point).title("Point new"));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        GetMyLoc();
        return true;
    }

    public void GetMyLoc()
    {
        prevLocation = LocationServices.FusedLocationApi.getLastLocation(ourClient);
        if(prevLocation != null)
        {

            LatLng pos = new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        }
    }
}
