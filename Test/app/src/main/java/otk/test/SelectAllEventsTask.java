package otk.test;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * Created by Andrew on 12/3/2015.
 */
public class SelectAllEventsTask extends AsyncTask<String, Void, JSONArray> {
    // http://findme-env.elasticbeanstalk.com/selectallevents.php

    Context context;

    SelectAllEventsTask(Context context) {
        this.context = context;
    }
    @Override
    protected JSONArray doInBackground(String... url) {
        return loadJSONArray(url[0]);
    }

    protected void onPostExecute(JSONArray jsonArray) {
        populateVector(jsonArray);
        ((MyApplication) context.getApplicationContext()).logEventList();
    }

    public JSONArray loadJSONArray(String url) {
        InputStream inputStream = null;
        JSONArray jsonArray = null;
        String json = "";

        // get inputstream from url
        try {
            URL urlname = new URL(url);
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
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        return jsonArray;
    }


    public void populateVector(JSONArray jsonArray) {
        // parse jsonArray into eventData objects
        try{
            ((MyApplication) context.getApplicationContext()).clearEventStorage();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //String id = jsonObject.getString("id");
                String title = jsonObject.getString("title");
                String creator = jsonObject.getString("creator");
                //String date = jsonObject.getString("date");
                String description = jsonObject.getString("description");
                String imagepath = jsonObject.getString("imagepath");
                String lat = jsonObject.getString("lat");
                String lng = jsonObject.getString("lng");

                MarkerOptions location = new MarkerOptions().title(title).position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                Date time = new Date();

                ((MyApplication) context.getApplicationContext()).addToEventList(new EventData(creator, title, description, imagepath, location, time));
            }
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
    }

}
