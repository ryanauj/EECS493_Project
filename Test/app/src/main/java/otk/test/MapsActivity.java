package otk.test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener{

    private GoogleMap mMap;
    private GoogleApiClient ourClient;
    private Location prevLocation;
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
        prevLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(prevLocation!=null) {
            LatLng cur_loc = new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(cur_loc).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cur_loc));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        mMap.setMyLocationEnabled(true);
        }

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
        mMap.addMarker(new MarkerOptions().position(point).title("Current Location"));

    }

    public void GetMyLoc()
    {
        prevLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(prevLocation != null)
        {
            LatLng pos = new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        }
        else
        {
        }
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

    public class LocationFailedDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Location Failed To Load")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


}
