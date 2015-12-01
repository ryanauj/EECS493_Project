package otk.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class Event_Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // testing sample
        //EventData sampleData = new EventData("Andrew","Beer Pong","Shooting some pong","/null",new MarkerOptions().position(new LatLng(0,0)).title("Beer Pong"), new Date());

        //Intent intent = getIntent();
        //EventData sampleData = intent.getParcelableExtra(EventListAdapter.EVENT_DATA_STRING);

        EventData sampleData = new EventData(((MyApplication) this.getApplication()).getTempEvent());

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
