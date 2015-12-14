package otk.test;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.StreetViewPanoramaFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Event_Details extends AppCompatActivity {
    private String maxAttendees = "";
    private TextView totalAttendees;

//    private RecyclerView recyclerView;
//    private RecyclerForumAdapter recyclerViewAdapter;
//    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ExpandableHeightListView forumList;
    private ForumListAdapter forumListAdapter;

    private View.OnClickListener addPostClicked;
    private View.OnClickListener addPostNotClicked;

    private EditText editPost;
    private Button cancelButton, addPost;

    private void disableButton(Button button) {
        button.setEnabled(false);
        button.setBackgroundColor(Color.LTGRAY);
    }

    private void backToDefaultForumConfig() {
        addPost.setText("Add Post");
        addPost.setBackgroundResource(android.R.drawable.btn_default);
        editPost.setVisibility(View.GONE);
        cancelButton.setEnabled(false);
        cancelButton.setVisibility(View.INVISIBLE);
        addPost.setOnClickListener(addPostClicked);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        final EventData sampleData = new EventData(((MyApplication) this.getApplication()).getTempEvent());

        final String loggedInUser = (((MyApplication) getApplication()).getUser().getUserName());

        maxAttendees = getResources().getString(R.string.infinity);

        // find the xml views by id
        TextView creator = (TextView) findViewById(R.id.event_creator);
        TextView title = (TextView) findViewById(R.id.event_title);
        TextView description = (TextView) findViewById(R.id.event_description);
        //ImageView picture = (ImageView) findViewById(R.id.event_picture);
        TextView time = (TextView) findViewById(R.id.event_time);
        TextView endtime = (TextView) findViewById(R.id.event_endtime);
        totalAttendees  = (TextView) findViewById(R.id.total_attendees);

        final Button rsvp = (Button) findViewById(R.id.rsvp_button);
        rsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleData.addAttendee(loggedInUser);
                disableButton(rsvp);

                totalAttendees.setText(String.valueOf(sampleData.getAttendees().size()) +
                                        " / " + maxAttendees);
                ((MyApplication) getApplication()).setTempEvent(sampleData);
            }
        });

        forumList = (ExpandableHeightListView) findViewById(R.id.forum_list_view);
        forumListAdapter = new ForumListAdapter(this, R.layout.forum_post_card, sampleData.getForumList());
        forumList.setAdapter(forumListAdapter);
        forumList.setExpanded(true);



        editPost = (EditText) findViewById(R.id.edit_post_text);
        cancelButton = (Button) findViewById(R.id.cancel_post);
        addPost = (Button) findViewById(R.id.add_post_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToDefaultForumConfig();
            }
        });

        addPostClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost.setText("Post");
                addPost.setBackgroundColor(Color.rgb(96, 168, 58));
                editPost.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                cancelButton.setEnabled(true);
                addPost.setOnClickListener(addPostNotClicked);
                CreateEvent.hideSoftKeyboard(Event_Details.this);
            }
        };

        addPostNotClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleData.addMessageToForum(loggedInUser, editPost.getText().toString());
                forumListAdapter.notifyDataSetChanged();
                backToDefaultForumConfig();
                ((MyApplication) getApplication()).setTempEvent(sampleData);
            }
        };

        addPost.setOnClickListener(addPostClicked);

//        sampleData.addMessageToForum(new ForumPost("Person", "Message 1!"));
//        sampleData.addMessageToForum(new ForumPost("Stan Barathean", "Message 2!"));

        // set the xml views with EventData
        if(sampleData != null) {
            if(creator!=null)
                creator.setText("Hosted by "+sampleData.getCreator());
                //creator.setText(((MyApplication) getApplication()).getUser().getUserName());
            if(title!=null)
                title.setText(sampleData.getTitle());
            if(description!=null)
                description.setText(sampleData.getDescription());
            // picture.setImage?
            if(time!=null)
                time.setText("Starts at "+getTimeString(sampleData.getTime())+" on "+getDateString(sampleData.getTime()));
            if(endtime!=null)
                endtime.setText("Ends at "+getTimeString(sampleData.getEndTime())+" on "+getDateString(sampleData.getEndTime()));

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


            if (sampleData.getMaxAttendees() > 0) {
                maxAttendees = String.valueOf(sampleData.getAttendees().size());
            }

            if (totalAttendees != null) {
                totalAttendees.setText(String.valueOf(sampleData.getAttendees().size()) +
                                       " / " + maxAttendees);
            }

            Log.e("Logged in user", loggedInUser);
            Log.e("Event creator", sampleData.getCreator());

            if (sampleData.getCreator().equals(loggedInUser) == true ||
                    sampleData.getAttendees().contains(loggedInUser) == true ||
                    (    sampleData.getMaxAttendees() > 0 &&
                           sampleData.getAttendees().size() >= sampleData.getMaxAttendees()
                    )
                    ) {
                disableButton(rsvp);
            }
        }
        else
            finish();
    }

    public String getTimeString(Calendar cal) {
        Date date = cal.getTime();
        SimpleDateFormat sdf;
        if (cal.get(Calendar.HOUR) > 9) {
            sdf = new SimpleDateFormat("hh:mm aa");
        }
        else {
            sdf = new SimpleDateFormat("h:mm aa");
        }
        return sdf.format(date);
    }

    public String getDateString(Calendar cal) {
        Date date = cal.getTime();
        SimpleDateFormat sdf;
        if (cal.get(Calendar.DAY_OF_MONTH) > 9) {
            sdf = new SimpleDateFormat("EEEE, MMM dd");
        }
        else {
            sdf = new SimpleDateFormat("EEEE, MMM d");
        }
        return sdf.format(date);
    }
}
