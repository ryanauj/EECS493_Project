package otk.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView currentuser = (TextView) findViewById(R.id.currentuser);
        String currentusername = ((MyApplication) getApplication()).getUser().getUserName();
        currentuser.setText(currentusername);

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);

                UserLoginTask login = new UserLoginTask(getApplicationContext(),username.getText().toString(),password.getText().toString());
                login.execute("http://findme-env.elasticbeanstalk.com/userlogin.php");
            }
        });
    }
}
