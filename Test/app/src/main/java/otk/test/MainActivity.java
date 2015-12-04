package otk.test;

import android.content.ClipData;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.Window;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.Activity;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private EventListAdapter adapter;
    //private ArrayAdapter<EventData> adapter;

    //private List<EventData> eventListStorage = new LinkedList<EventData>();

    //All Google Map Variables
    private GoogleMap mMap;
    private MapsActivity mapClass = new MapsActivity();

    private int windowHeight;

    //private LocationManager locMang;
    private Boolean GPSEnabled, NWEnabled;

    private long LOCATION_REFRESH_TIME = 100000;
    private float LOCATION_REFRESH_DISTANCE = 50;
    private boolean Location_Services_On = false;


    static final int CREATE_EVENT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        UserData initUser = new UserData("Tim");
        ((MyApplication) getApplication()).setUser(initUser);

        Log.d("APP STARTED", "HELLO");

        windowHeight = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();

        View splitter = findViewById(R.id.layout_draggable);
        /*
        splitter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                }

                return true;
            }
        });
        */

        /*splitter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    v.setVisibility(View.INVISIBLE);
                    Log.d("DRAG EVENT STARTED", "!!!!");
                    return true;
                }

                return false;
            }
        });
        splitter.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
                    Log.d("ACTION_DRAG_LOCATION", Float.toString(event.getY()));
                }
                Log.d("DRAG EVENT", "!!!!");
                return true;
            }
        });

        Log.d("DRAG LISTENER SET", "---");*/

        //Initialize Map
        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(mapClass);
        mapFragment.getMap().setOnMapClickListener(mapClass);

        //Default is going to be null
        //If the event is null then don't fill anything
        //Once the event is adjusted by create event AND placed in the list, it will return to null to signify no waiting events
        //EventData initEvent = new EventData("","","","/null",new MarkerOptions().position(new LatLng(0,0)).title(""), new Date());
        //((MyApplication) getApplication()).setTempEvent(initEvent);
        //eventListStorage.add(initEvent);

        UserData mainUser = new UserData("Stannis Baratheon");
        ((MyApplication) getApplication()).setUser(mainUser);

        mapFragment.getMap().setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                //eventParcelable parcelToSend= new eventParcelable(point.latitude, point.longitude, "Tim", "TempParcel","Just a temporary thing","Home");
                ((MyApplication) getApplication()).setTempEvent(new EventData());
                ((MyApplication) getApplication()).getTempEvent().setLocation(point);
                mapFragment.getView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                Intent createEventIntent = new Intent(MainActivity.this, CreateEvent.class);
                //createEventIntent.putExtra("event", parcelToSend);
                startActivityForResult(createEventIntent, CREATE_EVENT_REQUEST);
            }
        });

        //Create Event Button
        Button createEvent = (Button) findViewById(R.id.createevent);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateEvent.class);
                startActivity(intent);
            }
        });


        adapter =new EventListAdapter(this,R.layout.event_list_card, ((MyApplication) getApplication()).getEventStorage());
        ListView listView1 = (ListView) findViewById(R.id.eventListView);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventData data = (EventData) parent.getItemAtPosition(position);
                ((MyApplication) getApplication()).setTempEvent(data);
                Intent intent = new Intent(MainActivity.this, Event_Details.class);
                startActivity(intent);
            }
        });

        listView1.setAdapter(adapter);


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

            public void initMap() {
                MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
                mapFrag.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        // Add a marker in Sydney and move the camera
                        LatLng sydney = new LatLng(-34, 151);
                        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
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
                adapter.notifyDataSetChanged();
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

}


