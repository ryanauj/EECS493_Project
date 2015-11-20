package otk.test;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    private GoogleMap mMap;
    private MapsActivity mapClass = new MapsActivity();

    private long LOCATION_REFRESH_TIME = 10000;
    private float LOCATION_REFRESH_DISTANCE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(mapClass);
        mapFragment.getMap().setOnMapClickListener(mapClass);

        Button createEvent = (Button) findViewById(R.id.createevent);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateEvent.class);
                startActivity(intent);
            }
        });
        mapClass.mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mapClass.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this);
        mapClass.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
    }


            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_main, menu);
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_settings) {
                    return true;
                }

                return super.onOptionsItemSelected(item);
            }

            public void initMap() {
                MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
                mapFrag.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        // Add a marker in Sydney and move the camera
                        LatLng sydney = new LatLng(-34, 151);
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    }
                });
            }

            @Override
            protected void onStart() {
                super.onStart();
                if (!mResolvingError)
                    mapClass.mGoogleApiClient.connect();
            }

            @Override
            protected void onStop() {
                mapClass.mGoogleApiClient.disconnect();
                super.onStop();
            }

            @Override
            public void onConnected(Bundle connectionHint) {
                mapClass.mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();

            }

            @Override
            public void onConnectionSuspended(int cause) {
                // The connection has been interrupted.
                // Disable any UI components that depend on Google APIs
                // until onConnected() is called.
            }

            @Override
            public void onConnectionFailed(ConnectionResult result) {
                if (mResolvingError) {
                    return;
                } else if (result.hasResolution()) {
                    try {
                        mResolvingError = true;
                        result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
                    } catch (IntentSender.SendIntentException e) {
                        mapClass.mGoogleApiClient.connect();
                    }
                } else {
                    showErrorDialog(result.getErrorCode());
                    mResolvingError = true;
                }
            }

            private void showErrorDialog(int errorCode) {
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                Bundle args = new Bundle();
                args.putInt(DIALOG_ERROR, errorCode);
                dialogFragment.setArguments(args);
                dialogFragment.show(getFragmentManager(), "errordialog");
            }

            public void onDialogDismissed() {
                mResolvingError = false;
            }

            public static class ErrorDialogFragment extends DialogFragment {
                public ErrorDialogFragment() {
                }

                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    int errorCode = this.getArguments().getInt(DIALOG_ERROR);
                    return GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
                }

                @Override
                public void onDismiss(DialogInterface dialog) {
                    ((MainActivity) getActivity()).onDialogDismissed();
                }
            }

            @Override
            protected void onSaveInstanceState(Bundle outState) {
                super.onSaveInstanceState(outState);
                outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
            }

            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(getApplicationContext(), "Made it here", Toast.LENGTH_LONG).show();
                mapClass.curLocation = location;
                LatLng pos = new LatLng(mapClass.curLocation.getLatitude(), mapClass.curLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
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


