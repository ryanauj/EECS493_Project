package otk.test;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.common.ConnectionResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    //All Google Map Variables
    private GoogleMap mMap;
    private MapsActivity mapClass = new MapsActivity();

    private Boolean GPSEnabled, NWEnabled;

    private long LOCATION_REFRESH_TIME = 100000;
    private float LOCATION_REFRESH_DISTANCE = 50;
    private boolean Location_Services_On = false;

    private RecyclerView recyclerView;
    private RecyclerEventListAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    static final int CREATE_EVENT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("APP STARTED", "HELLO");

        // check for logged in user through
        if (fileExists(this,"userdata")) {
            String username = null;
            try {
                FileInputStream fis = openFileInput("userdata");
                byte[] input = new byte[fis.available()];
                while (fis.read(input) != -1) {}
                username = new String(input);
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (username != null && !username.equals("Not Logged In")) {
                ((MyApplication) getApplication()).setUser(new UserData(username));
            }
            else {
                // re-route to login
                Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);
            }
        }
        else {
            // create userdata file
            try {
                FileOutputStream fos = openFileOutput("userdata", Context.MODE_PRIVATE);
                fos.close();

                // re-route to login
                Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Initialize Map
        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(mapClass);
        mapFragment.getMap().setOnMapClickListener(mapClass);

        mapFragment.getMap().setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                ((MyApplication) getApplication()).setTempEvent(new EventData());
                ((MyApplication) getApplication()).getTempEvent().setLocation(point);
                mapFragment.getView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                Intent createEventIntent = new Intent(MainActivity.this, CreateEvent.class);
                startActivityForResult(createEventIntent, CREATE_EVENT_REQUEST);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.eventRecyclerView);

        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        recyclerViewAdapter = new RecyclerEventListAdapter(this, R.layout.event_list_card,
                ((MyApplication) getApplication()).getEventStorage(),
                new RecyclerEventListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        EventData data = recyclerViewAdapter.getItemAtPosition(position);
                        mapClass.moveCamToLocationSmooth(data.getLocation().getPosition());
                    }
                },
                new RecyclerEventListAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View view, int position) {
                        Log.d("Recycler Item", "Long click");
                        EventData data = recyclerViewAdapter.getItemAtPosition(position);
                        ((MyApplication) getApplication()).setTempEvent(data);
                        Intent intent = new Intent(MainActivity.this, Event_Details.class);
                        startActivity(intent);
                    }
                }
        );
        recyclerView.setAdapter(recyclerViewAdapter);


        //Build Google Client to communicate with google play services
        mapClass.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        //Set location updates
        setLocationServices();
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
                Toast.makeText(getApplicationContext(), "Location Changed", Toast.LENGTH_LONG).show();
                mapClass.GetMyLoc();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CREATE_EVENT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                EventData newEvent = new EventData(((MyApplication) this.getApplication()).getTempEvent());
                final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
                mapFragment.getMap().addMarker(newEvent.getLocation().title(newEvent.getTitle()));

                ((MyApplication) getApplication()).addToEventList(newEvent);
                recyclerViewAdapter.notifyDataSetChanged();
               //mMap.addMarker(new MarkerOptions().position(point).title("Point new"));
            }
        }
    }

    public void setLocationServices()
    {
        try{
            mapClass.mLocationManager =  (LocationManager) getSystemService(LOCATION_SERVICE);
            GPSEnabled = mapClass.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            NWEnabled = mapClass.mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!GPSEnabled && !NWEnabled)
                Toast.makeText(getApplicationContext(), "No Services Currently Available", Toast.LENGTH_LONG).show();//No Location services available
            else
            {
                if(GPSEnabled)
                {
                    mapClass.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this);
                    mapClass.LocProvider = "GPS_PROVIDER";
                    Toast.makeText(getApplicationContext(), "GPS Services are being used", Toast.LENGTH_LONG).show();
                    Location_Services_On=true;
                }
                else if(NWEnabled)
                {
                    mapClass.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this);
                    mapClass.LocProvider = "NETWORK_PROVIDER";
                    Toast.makeText(getApplicationContext(), "Network Services are being used", Toast.LENGTH_LONG).show();
                    Location_Services_On=true;
                }
                if(mapClass.mLocationManager != null)
                {
                    mapClass.GetMyLoc();
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mapClass.mLocationManager.removeUpdates(this);
        Location_Services_On = false;

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setLocationServices();
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

}


