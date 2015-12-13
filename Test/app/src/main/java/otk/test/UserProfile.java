package otk.test;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        TextView usernameView = (TextView) findViewById(R.id.username);
        LinearLayout usercolorView = (LinearLayout) findViewById(R.id.usercolor);

        UserData currentUser = ((MyApplication) getApplication()).getUser();

        usernameView.setText(currentUser.getUserName());
        Log.e("color",currentUser.getColorValue()+"");
        usercolorView.setBackgroundColor(ContextCompat.getColor(this, currentUser.getColorValue()));

    }
}
