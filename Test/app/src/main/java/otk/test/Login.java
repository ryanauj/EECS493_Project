package otk.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // for testing, show the current logged in user
        TextView currentuser = (TextView) findViewById(R.id.currentuser);
        String currentusername = ((MyApplication) getApplication()).getUser().getUserName();
        currentuser.setText(currentusername);

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);

                new UserLoginTask(username.getText().toString(),password.getText().toString()).execute("http://findme-env.elasticbeanstalk.com/userlogin.php");

                //UserLoginTask login = new UserLoginTask(getApplicationContext(),username.getText().toString(),password.getText().toString());
                //login.execute("http://findme-env.elasticbeanstalk.com/userlogin.php");
            }
        });
    }

    public class UserLoginTask extends AsyncTask<String, Void, String> {
        // http://findme-env.elasticbeanstalk.com/userlogin.php

        String username = "";
        String password = "";

        public UserLoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... url) {
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
                return null;
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
                return null;
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
                return null;
            }

            return username;
        }

        protected void onPostExecute(String username) {
            if (username == null) {
                // failed to login, call AlertDialog from below
                alert("Incorrect username or password. Please try again.");
            }
            else {
                // login successful, set global user
                ((MyApplication) getApplication()).setUser(new UserData(username));
                
                // redirect to MainActivity
                //Intent intent = new Intent(Login.this, MainActivity.class);
                //startActivity(intent);
            }
        }
    }

    public void alert(String message) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue
                    }
                })
                .show();
    }
}
