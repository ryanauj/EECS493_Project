package otk.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.MarkerOptions;

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
        //ImageView picture = (ImageView) findViewById(R.id.event_picture);
        TextView location = (TextView) findViewById(R.id.event_location);
        TextView time = (TextView) findViewById(R.id.event_time);

        // set the xml views with EventData
        if(sampleData != null) {
            if(creator!=null)
                creator.setText(((MyApplication) getApplication()).getUser().getUserName());
            if(title!=null)
                title.setText(sampleData.getTitle());
            if(description!=null)
                description.setText(sampleData.getDescription());
            // picture.setImage?
            if(time!=null)
                time.setText(sampleData.getTime().toString());

            final StreetView svClass = new StreetView();
            final StreetViewPanoramaFragment svFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.locStreetView);
            if(svFragment!=null) {
                Log.e("Street View:","Loaded Fragment");
                //svFragment.getStreetViewPanoramaAsync(svClass);
                svFragment.getStreetViewPanorama().setUserNavigationEnabled(false);
                svFragment.getStreetViewPanorama().setPanningGesturesEnabled(true);
                svFragment.getStreetViewPanorama().setZoomGesturesEnabled(false);
                svFragment.getStreetViewPanorama().setStreetNamesEnabled(false);
                svFragment.getStreetViewPanorama().setPosition(sampleData.getLocation().getPosition(),10);
            }
        }
        else
            finish();
    }
}
