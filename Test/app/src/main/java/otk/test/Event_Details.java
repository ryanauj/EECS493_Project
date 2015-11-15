package otk.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Event_Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // testing sample
        EventData sampleData = new EventData("Andrew","Beer Pong","Shooting some pong","/null",new LatLng(0,0), new Date());

        // find the xml views by id
        TextView creator = (TextView) findViewById(R.id.event_creator);
        TextView title = (TextView) findViewById(R.id.event_title);
        TextView description = (TextView) findViewById(R.id.event_description);
        ImageView picture = (ImageView) findViewById(R.id.event_picture);
        TextView location = (TextView) findViewById(R.id.event_location);
        TextView time = (TextView) findViewById(R.id.event_time);

        // set the xml views with EventData
        creator.setText(sampleData.getCreator());
        title.setText(sampleData.getTitle());
        description.setText(sampleData.getDescription());
        // picture.setImage?
        location.setText(sampleData.getLocation().toString());
        time.setText(sampleData.getTime().toString());
    }
}
