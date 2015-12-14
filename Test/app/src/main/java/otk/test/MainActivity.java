package otk.test;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;

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
        if (fileExists("userdata") && fileExists("colordata")) {
            String username = null;
            int color = -40;
            try {
                FileInputStream fis = openFileInput("userdata");
                byte[] input = new byte[fis.available()];
                int count = 0;
                while (fis.read(input) != -1 && count < 1000) {
                    count = count + 1;
                }
                if (count >= 1000) {
                    Log.e("inf loop1", "inf loop occurred in userdata");
                }
                username = new String(input);
                fis.close();

                FileInputStream fis2 = openFileInput("colordata");
                byte[] colorinput = new byte[fis2.available()];
                count = 0;
                while (fis2.read(colorinput) != -1 && count < 1000) {
                    count = count + 1;
                }
                if (count >= 1000) {
                    Log.e("inf loop2", "inf loop occurred in colordata");
                }
                color = Integer.valueOf(new String(colorinput));
                fis2.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (username != null && !username.equals("Not Logged In") && color != -40) {
                ((MyApplication) getApplication()).setUser(new UserData(username, color));
            } else {
                // re-route to login
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        } else {
            // create userdata file
            try {
                FileOutputStream fos = openFileOutput("userdata", Context.MODE_PRIVATE);
                fos.write("Not Logged In".getBytes());
                fos.close();
                FileOutputStream fos2 = openFileOutput("colordata", Context.MODE_PRIVATE);
                fos2.write("0".getBytes());
                fos2.close();

                // re-route to login
                Intent intent = new Intent(MainActivity.this, Login.class);
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

                // display all events
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.eventRecyclerView);

        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        recyclerViewAdapter = new RecyclerEventListAdapter(this, R.layout.event_list_card,
                ((MyApplication) getApplication()).getEventStorage(),
                ((MyApplication) getApplication()).getUser().getColorValue(),
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

        new SelectAllEventsTask().execute("http://findme-env.elasticbeanstalk.com/selectallevents.php");
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
        switch (id) {
            case R.id.actionlogout: {
                ((MyApplication) getApplication()).setUser(new UserData("Not Logged In", 0));

                try {
                    FileOutputStream fos = openFileOutput("userdata", Context.MODE_PRIVATE);
                    fos.write("Not Logged In".getBytes());
                    fos.close();
                    FileOutputStream fos2 = openFileOutput("colordata", Context.MODE_PRIVATE);
                    fos2.write("0".getBytes());
                    fos2.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
                return true;
            }
            case R.id.actionprofile: {
                Intent intent = new Intent(MainActivity.this, UserProfile.class);
                startActivity(intent);
                return true;
            }
            case R.id.actionhelp: {
                Intent intent = new Intent(MainActivity.this, Help.class);
                startActivity(intent);
                return true;
            }
            default: {
                return false;
            }
        }

        //return super.onOptionsItemSelected(item);
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

                newEvent.setColor(((MyApplication) getApplication()).getUser().getColorValue());
                Log.e("color", ((MyApplication) getApplication()).getUser().getColorValue() + "");
                ((MyApplication) getApplication()).addToEventList(newEvent);

                float color = getMarkerColor(newEvent.getColor());
                MarkerOptions location = new MarkerOptions().title(newEvent.getTitle()).position(newEvent.getLocation().getPosition()).icon(BitmapDescriptorFactory.defaultMarker(color));
                recyclerViewAdapter.notifyDataSetChanged();
                mapFragment.getMap().addMarker(location);
                //mMap.addMarker(new MarkerOptions().position(point).title("Point new"));
            }
        }
    }

    public void setLocationServices() {
        try {
            mapClass.mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            GPSEnabled = mapClass.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            NWEnabled = mapClass.mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!GPSEnabled && !NWEnabled)
                Toast.makeText(getApplicationContext(), "No Services Currently Available", Toast.LENGTH_LONG).show();//No Location services available
            else {
                if (GPSEnabled) {
                    try {
                        mapClass.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this);
                    }
                    catch (SecurityException e) {
                        Log.e("Security",e.getMessage());
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
                    }
                    mapClass.LocProvider = "GPS_PROVIDER";
                    Toast.makeText(getApplicationContext(), "GPS Services are being used", Toast.LENGTH_LONG).show();
                    Location_Services_On = true;
                } else if (NWEnabled) {
                    try {
                        mapClass.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this);
                    }
                    catch (SecurityException e) {
                        Log.e("Security",e.getMessage());
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
                    }
                    mapClass.LocProvider = "NETWORK_PROVIDER";
                    Toast.makeText(getApplicationContext(), "Network Services are being used", Toast.LENGTH_LONG).show();
                    Location_Services_On = true;
                }
                if (mapClass.mLocationManager != null) {
                    mapClass.GetMyLoc();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mapClass.mLocationManager.removeUpdates(this);
        }
        catch (SecurityException e) {
            Log.e("Security",e.getMessage());
        }
        Location_Services_On = false;

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setLocationServices();
    }

    public boolean fileExists(String filename) {
        File file = getBaseContext().getFileStreamPath(filename);
        return file.exists();
    }

    public class SelectAllEventsTask extends AsyncTask<String, Void, JSONArray> {
        // http://findme-env.elasticbeanstalk.com/selectallevents.php

        @Override
        protected JSONArray doInBackground(String... url) {
            return loadJSONArray(url[0]);
        }

        protected void onPostExecute(JSONArray jsonArray) {
            populateVector(jsonArray);
            //((MyApplication) getApplicationContext()).logEventList();
        }

        public JSONArray loadJSONArray(String url) {
            InputStream inputStream = null;
            JSONArray jsonArray = null;
            String json = "";

            // get inputstream from url
            try {
                URL urlname = new URL(url);
                HttpURLConnection testconn = (HttpURLConnection) urlname.openConnection();
                if (testconn.getResponseCode() == 404) {
                    // no events to get
                    return null;
                }
                testconn.disconnect();
                URLConnection conn = urlname.openConnection();
                inputStream = conn.getInputStream();

            } catch (MalformedURLException e) {
                Log.e("MalformedURLException", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            }

            // read content into string
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                inputStream.close();
                json = stringBuilder.toString();
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            }

            // create jsonArray from string
            if (json != "") {
                try {
                    jsonArray = new JSONArray(json);
                } catch (JSONException e) {
                    Log.e("JSONException", e.getMessage());
                }
            }

            return jsonArray;
        }


        public void populateVector(JSONArray jsonArray) {
            // parse jsonArray into eventData objects
            if (jsonArray == null) {
                return;
            }
            try{
                ((MyApplication) getApplicationContext()).clearEventStorage();
                final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String title = jsonObject.getString("title");
                    String creator = jsonObject.getString("creator");
                    String date = jsonObject.getString("date");
                    String description = jsonObject.getString("description");
                    String lat = jsonObject.getString("lat");
                    String lng = jsonObject.getString("lng");
                    String colorvalue = jsonObject.getString("color");
                    String endDate = jsonObject.getString("endDate");

                    float color = getMarkerColor(Integer.parseInt(colorvalue));

                    MarkerOptions location = new MarkerOptions().title(title).position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).icon(BitmapDescriptorFactory.defaultMarker(color));
                    Calendar time = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                    try {
                        time.setTime(sdf.parse(date));// all done
                    }
                    catch (ParseException e) {
                        Log.e("ParseExcept",e.getMessage());
                    }

                    Calendar endTime = Calendar.getInstance();
                    try {
                        endTime.setTime(sdf.parse(endDate));// all done
                    }
                    catch (ParseException e) {
                        Log.e("ParseExcept",e.getMessage());
                    }

                    // Defaults
                    int max_attend = 0; // 0 means no limit on attendance
                    String pictureUrl = "";
                    HashSet attendees = new HashSet();
                    LinkedList<ForumPost> forum_list = new LinkedList<>();

                    EventData newEvent = new EventData(id, creator, title, description, location, time, endTime, max_attend, Integer.valueOf(colorvalue), attendees, forum_list);

                    // check for expired events
                    Calendar currenttime = Calendar.getInstance();
                    if (currenttime.after(newEvent.getEndTime())) {
                        // delete event
                        new DeleteEventTask(newEvent.getId()).execute("http://findme-env.elasticbeanstalk.com/deleteevent.php");
                    }
                    else {
                        mapFragment.getMap().addMarker(newEvent.getLocation().title(newEvent.getTitle()));
                        ((MyApplication) getApplicationContext()).addToEventList(newEvent);
                    }

                }
                // done loading all events
                recyclerViewAdapter.setList(((MyApplication) getApplicationContext()).getEventStorage());
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
            }
        }

    }

    public class DeleteEventTask extends AsyncTask<String, Void, Boolean> {
        // http://findme-env.elasticbeanstalk.com/deleteevent.php

        int result;
        String id = "";

        public DeleteEventTask(String id) {
            this.id = id;
        }

        @Override
        protected Boolean doInBackground(String... url) {
            try {
                URL urlname = new URL(url[0]);
                HttpURLConnection conn = (HttpURLConnection) urlname.openConnection();
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setChunkedStreamingMode(0);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("id", id);

                Log.e("json", jsonParam.toString());

                wr.writeBytes(jsonParam.toString());

                wr.flush();
                wr.close();

                result = conn.getResponseCode();
                Log.e("code",result+"");
                conn.disconnect();

            } catch (MalformedURLException e) {
                Log.e("MalformedURL", e.getMessage());
                return false;
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
                return false;
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
                return false;
            }

            return true;
        }

        protected void onPostExecute(Boolean noException) {
            if (noException) {
                if (result == 200) {
                    Log.e("DeleteEvent","Success");

                }
                else if (result == 400) {
                    Log.e("DeleteEvent","Failure no deletion");

                }
                else {
                    Log.e("DeleteEvent","Failure unknown error");
                }
            }
            else {
                Log.e("DeleteEvent","Failure");
            }
        }
    }

    public float getMarkerColor(int resourceid) {
        int color = ContextCompat.getColor(this, resourceid);
        float[] newcolor = {0,0,0};
        Color c = new Color();
        c.colorToHSV(color,newcolor);

        return newcolor[0];
    }

}


