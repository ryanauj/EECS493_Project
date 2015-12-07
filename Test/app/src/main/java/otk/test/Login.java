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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

        Button login = (Button) findViewById(R.id.loginbutton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);

                new UserLoginTask(username.getText().toString(), password.getText().toString()).execute("http://findme-env.elasticbeanstalk.com/userlogin.php");

                //UserLoginTask login = new UserLoginTask(getApplicationContext(),username.getText().toString(),password.getText().toString());
                //login.execute("http://findme-env.elasticbeanstalk.com/userlogin.php");
            }
        });

        Button logout = (Button) findViewById(R.id.logoutbutton);
        if (((MyApplication) getApplication()).getUser().getUserName().equals("Not Logged In")) {
            logout.setVisibility(View.GONE);
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) getApplication()).setUser(new UserData("Not Logged In"));
                String FILENAME = "userdata";

                try {
                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write("Not Logged In".getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button createuser = (Button) findViewById(R.id.createuser);
        createuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, CreateUser.class);
                startActivity(intent);
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
            String result;
            try {
                URL urlname = new URL(url[0]);
                HttpURLConnection conn = (HttpURLConnection) urlname.openConnection();
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
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

                result = conn.getResponseCode()+"";
                conn.disconnect();

            } catch (MalformedURLException e) {
                Log.e("MalformedURL", e.getMessage());
                return e.getMessage();
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
                return e.getMessage();
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
                return e.getMessage();
            }

            return result;
        }

        protected void onPostExecute(String result) {
            if(result.equals("200")) {
                // successful login
                ((MyApplication) getApplication()).setUser(new UserData(username));
                String FILENAME = "userdata";

                try {
                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(username.getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
            else if(result.equals("400")) {
                // incorrect login
                alert("Incorrect username or password. Please try again.");
            }
            else {
                // unknown error
                alert("ERROR: "+result);
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
