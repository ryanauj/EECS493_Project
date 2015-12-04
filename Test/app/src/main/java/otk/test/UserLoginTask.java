package otk.test;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Andrew on 12/3/2015.
 */
public class UserLoginTask extends AsyncTask<String, Void, Boolean> {
    // http://findme-env.elasticbeanstalk.com/userlogin.php

    Context context;
    UserData user;
    String username = "";
    String password = "";

    public UserLoginTask(Context context, String username, String password) {
        this.context = context;
        this.username = username;
        this.password = password;
    }

    @Override
    protected Boolean doInBackground(String... url) {
        try {
            URL urlname = new URL(url[0]);
            HttpURLConnection conn = (HttpURLConnection) urlname.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //conn.setRequestProperty("charset", "utf-8");
            //conn.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            //conn.setUseCaches(false);
            conn.setChunkedStreamingMode(0);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("username", username);
            jsonParam.put("password", password);

            wr.writeBytes(jsonParam.toString());

            wr.flush();
            wr.close();

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

        user.setUserName(username);

        return true;
    }

    protected void onPostExecute(Boolean success) {
        if (success) {
            Log.e("UserLogin", "Success");
            ((MyApplication) context.getApplicationContext()).setUser(user);
        }
        else {
            Log.e("UserLogin","Failure");
        }
    }
}
