package otk.test;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Vector;

public class testconnection extends AppCompatActivity {

    Vector<EventData> eventList = new Vector<>();

    URL urlname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testconnection);

        // execute async task
        //new DownloadAllEventData().execute("http://findme-env.elasticbeanstalk.com/selectallevents.php");

        EventData event = new EventData();
        event.setTitle("Test Event upload");
        event.setCreator("Andrew");
        event.setDescription("this is a test upload");
        event.setLocation(new LatLng(10.00, 10.00));
        event.setTime(new Date());
        event.setNameOfLocation("Andrew's House");

        new UploadEventData(event).execute("http://findme-env.elasticbeanstalk.com/addevent.php");
    }
}
