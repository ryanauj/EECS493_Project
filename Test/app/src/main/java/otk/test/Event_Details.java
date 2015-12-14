package otk.test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.StreetViewPanoramaView;

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

    private ScrollView scrollView;
    private LinearLayout main_linear_layout;
    private LinearLayout map_linear_layout;

    StreetViewPanoramaView streetViewPanoramaView;

    private void disableButton(Button button) {
        button.setEnabled(false);
        button.setBackgroundColor(Color.LTGRAY);
    }

    private void hideSoftKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void backToDefaultForumConfig() {
        addPost.setText("Add Post");
        addPost.setBackgroundResource(android.R.drawable.btn_default);
        editPost.setVisibility(View.GONE);
        editPost.setText("");
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
        scrollView = (ScrollView) findViewById(R.id.event_details_scrollview);
        main_linear_layout = (LinearLayout) findViewById(R.id.event_details_layout);
        TextView creator = (TextView) findViewById(R.id.event_creator);
        TextView title = (TextView) findViewById(R.id.event_title);
        TextView description = (TextView) findViewById(R.id.event_description);
        //ImageView picture = (ImageView) findViewById(R.id.event_picture);
        TextView time = (TextView) findViewById(R.id.event_time);
        TextView endtime = (TextView) findViewById(R.id.event_endtime);
        totalAttendees  = (TextView) findViewById(R.id.total_attendees);
        //View locStreetView = findViewById(R.id.locStreetView);
        streetViewPanoramaView = (StreetViewPanoramaView) findViewById(R.id.street_view_panorama);

        main_linear_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(main_linear_layout);
                return false;
            }
        });

//        locStreetView.setOnTouchListener(new View.OnTouchListener() {
//            // Setting on Touch Listener for handling the touch inside ScrollView
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // Disallow the touch request for scrollview on touch of child view
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                scrollView.requestDisallowInterceptTouchEvent(true);
//                Log.e("Map touched", "requested disallow interceptTouchEvents from parent layouts");
//                return false;
//            }
//        });

//        locStreetView.setFocusableInTouchMode(true);

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
        editPost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editPost, InputMethodManager.SHOW_FORCED);
                }
            }
        });

        cancelButton = (Button) findViewById(R.id.cancel_post);
        addPost = (Button) findViewById(R.id.add_post_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToDefaultForumConfig();
                hideSoftKeyboard(cancelButton);
                forumList.requestFocus();
            }
        });

        addPostClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost.setText("Post");
                addPost.setBackgroundColor(Color.rgb(0, 173, 111));
                editPost.setVisibility(View.VISIBLE);
                editPost.requestFocus();
                cancelButton.setVisibility(View.VISIBLE);
                cancelButton.setEnabled(true);
                addPost.setOnClickListener(addPostNotClicked);
            }
        };

        addPostNotClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(addPost);
                sampleData.addMessageToForum(loggedInUser, editPost.getText().toString());
                forumListAdapter.notifyDataSetChanged();
                backToDefaultForumConfig();
                ((MyApplication) getApplication()).setTempEvent(sampleData);
                forumList.requestFocus();
            }
        };

        addPost.setOnClickListener(addPostClicked);

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

//            final StreetView svClass = new StreetView();
//            final StreetViewPanoramaFragment svFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.locStreetView);
//            if(svFragment!=null) {
//                Log.e("Street View:","Loaded Fragment");
//                //svFragment.getStreetViewPanoramaAsync(svClass);
//                svFragment.getStreetViewPanorama().setUserNavigationEnabled(false);
//                svFragment.getStreetViewPanorama().setPanningGesturesEnabled(true);
//                svFragment.getStreetViewPanorama().setZoomGesturesEnabled(false);
//                svFragment.getStreetViewPanorama().setStreetNamesEnabled(false);
//                svFragment.getStreetViewPanorama().setPosition(sampleData.getLocation().getPosition(),30);
//            }

            if(streetViewPanoramaView!=null) {
                Log.e("Street View:","Loaded Fragment");
                //svFragment.getStreetViewPanoramaAsync(svClass);
                streetViewPanoramaView.getStreetViewPanorama().setUserNavigationEnabled(false);
                streetViewPanoramaView.getStreetViewPanorama().setPanningGesturesEnabled(true);
                streetViewPanoramaView.getStreetViewPanorama().setZoomGesturesEnabled(false);
                streetViewPanoramaView.getStreetViewPanorama().setStreetNamesEnabled(false);
                streetViewPanoramaView.getStreetViewPanorama().setPosition(sampleData.getLocation().getPosition(),30);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        Rect rect = new Rect(0, 0, streetViewPanoramaView.getWidth(), streetViewPanoramaView.getHeight());

        if (streetViewPanoramaView != null) {
            streetViewPanoramaView.getGlobalVisibleRect(rect);

            if (rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                event.offsetLocation(-rect.left, -rect.top);
                streetViewPanoramaView.dispatchTouchEvent(event);
                return true;

            } else {
                //Continue with touch event
                return super.dispatchTouchEvent(event);
            }
        } else {
            return super.dispatchTouchEvent(event);
        }
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
