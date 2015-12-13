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
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CreateUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText confirmpassword = (EditText) findViewById(R.id.confirmpassword);
        Button createuser = (Button) findViewById(R.id.createuser);
        Button cancel = (Button) findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateUser.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        createuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if fields are filled in
                if (username.getText().toString().isEmpty()) {
                    alert("Please enter a username.");
                    return;
                }
                if (password.getText().toString().isEmpty()) {
                    alert("Please enter a password.");
                    return;
                }

                // check if passwords match
                if (password.getText().toString().equals(confirmpassword.getText().toString())) {
                    // create the user
                    RadioGroup color_selec = (RadioGroup) findViewById(R.id.color_selector);
                    int colorValue = 0xff59a6a6;
                    Log.e("color3",colorValue+"");
                    switch (color_selec.getCheckedRadioButtonId())
                    {
                        case R.id.user_red: colorValue = R.color.red;
                            break;
                        case R.id.user_blue: colorValue = R.color.blue;
                            break;
                        case R.id.user_green: colorValue = R.color.green;
                            break;
                        case R.id.user_orange: colorValue = R.color.orange;
                            break;
                        case R.id.user_pink: colorValue = R.color.pink;
                            break;
                        case R.id.user_purple: colorValue = R.color.purple;
                            break;
                        case R.id.user_turquoise: colorValue = R.color.turquoise;
                            break;
                        case R.id.user_yellow: colorValue = R.color.yellow;
                            break;
                        default:
                            colorValue = R.color.appColor;
                    }
                    new CreateUserTask(username.getText().toString(),password.getText().toString(),colorValue).execute("http://findme-env.elasticbeanstalk.com/createuser.php");
                }
                else {
                    alert("Passwords do not match");
                    return;
                }
            }
        });
    }

    public class CreateUserTask extends AsyncTask<String, Void, String> {
        // http://findme-env.elasticbeanstalk.com/createuser.php

        String username = "";
        String password = "";
        int color_value = 0xff59a6a6;

        public CreateUserTask(String username, String password, int color_value) {
            this.username = username;
            this.password = password;
            this.color_value = color_value;
        }

        @Override
        protected String doInBackground(String... url) {
            String result = "";
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
                jsonParam.put("color",color_value);

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
                // successful user creation
                ((MyApplication) getApplication()).setUser(new UserData(username, color_value));
                try {
                    FileOutputStream fos = openFileOutput("userdata", Context.MODE_PRIVATE);
                    fos.write(username.getBytes());
                    fos.close();
                    FileOutputStream fos2 = openFileOutput("colordata", Context.MODE_PRIVATE);
                    fos2.write((color_value+"").getBytes());
                    fos2.close();
                }
                catch (FileNotFoundException e) {
                    Log.e("File",e.getMessage());
                } catch (IOException e) {
                    Log.e("io",e.getMessage());
                }
                Intent intent = new Intent(CreateUser.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else if(result.equals("401")) {
                // username already exists
                alert("Username already exists.");
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
