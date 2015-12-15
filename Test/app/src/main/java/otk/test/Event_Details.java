package otk.test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
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

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Event_Details extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {
    private EventData sampleData;
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

    private StreetViewPanoramaView streetViewPanoramaView;
    private StreetViewPanorama streetViewPanorama;

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

        sampleData = new EventData(((MyApplication) this.getApplication()).getTempEvent());

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

                ForumPost newForumPost = new ForumPost(loggedInUser, editPost.getText().toString(), Calendar.getInstance());

                sampleData.addMessageToForum(newForumPost);

                // create new chat message on database
                new CreateChatMessageTask(sampleData, newForumPost).execute("http://findme-env.elasticbeanstalk.com/createchatmessage.php");

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
                //Log.e("Street View:","Loaded Fragment");
                streetViewPanoramaView.onCreate(savedInstanceState);
                streetViewPanoramaView.getStreetViewPanoramaAsync(this);
            }


            if (sampleData.getMaxAttendees() > 0) {
                maxAttendees = String.valueOf(sampleData.getAttendees().size());
            }

            if (totalAttendees != null) {
                totalAttendees.setText(String.valueOf(sampleData.getAttendees().size()) +
                        " / " + maxAttendees);
            }

            if (sampleData.getCreator().equals(loggedInUser) == true ||
                    sampleData.getAttendees().contains(loggedInUser) == true ||
                    (    sampleData.getMaxAttendees() > 0 &&
                           sampleData.getAttendees().size() >= sampleData.getMaxAttendees()
                    )
                    ) {
                disableButton(rsvp);
            }
            new SelectChatMessagesTask(sampleData).execute("http://findme-env.elasticbeanstalk.com/selectchatmessages.php");
        }
        else
            finish();
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setUserNavigationEnabled(false);
        streetViewPanorama.setPanningGesturesEnabled(true);
        streetViewPanorama.setZoomGesturesEnabled(false);
        streetViewPanorama.setStreetNamesEnabled(false);
        streetViewPanorama.setPosition(sampleData.getLocation().getPosition(), 30);
        this.streetViewPanorama = streetViewPanorama;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        int[] l = new int[2];
        streetViewPanoramaView.getLocationOnScreen(l);
        int x = l[0];
        int y = l[1];

        Rect rect = new Rect(x, y, x + streetViewPanoramaView.getWidth(), y + streetViewPanoramaView.getHeight());

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

    public class CreateChatMessageTask extends AsyncTask<String, Void, Boolean> {
        // http://findme-env.elasticbeanstalk.com/deleteevent.php

        EventData event;
        ForumPost forumPost;
        int result;

        public CreateChatMessageTask(EventData event, ForumPost forumPost) {
            this.event = event;
            this.forumPost = forumPost;
        }

        @Override
        protected Boolean doInBackground(String... url) {
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
                jsonParam.put("eventid",event.getId());
                jsonParam.put("username",forumPost.getUser());
                jsonParam.put("date", forumPost.getTime().getTime().toString());
                jsonParam.put("message",forumPost.getMessage());

                wr.writeBytes(jsonParam.toString());

                wr.flush();
                wr.close();

                result = conn.getResponseCode();

                conn.disconnect();

            } catch (MalformedURLException e) {
                Log.e("MalformedURL", e.getMessage());
                return false;
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
                return false;
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
                return false;
            }

            return true;
        }

        protected void onPostExecute(Boolean noException) {
            if (noException) {
                if (result == 200) {
                    Log.e("createmessage","Success");
                }
                else if (result == 404) {
                    Log.e("createmessage","Failure, insert failed");
                }
                else {
                    Log.e("createmessage","Failure, unknown error");
                }
            }
            else {
                Log.e("createmessage","Failure, exception");
            }
        }
    }

    public class SelectChatMessagesTask extends AsyncTask<String, Void, JSONArray> {
        // http://findme-env.elasticbeanstalk.com/selectallevents.php

        EventData event;

        public SelectChatMessagesTask(EventData event) {
            this.event = event;
        }

        @Override
        protected JSONArray doInBackground(String... url) {
            return loadJSONArray(url[0]);
        }

        protected void onPostExecute(JSONArray jsonArray) {
            populateList(jsonArray);
            //((MyApplication) getApplicationContext()).logEventList();
        }

        public JSONArray loadJSONArray(String url) {
            InputStream inputStream = null;
            JSONArray jsonArray = null;
            String json = "";

            // get inputstream from url
            try {
                URL urlname = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) urlname.openConnection();
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setChunkedStreamingMode(0);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("eventid", event.getId());
                Log.e("eventid",event.getId());

                wr.writeBytes(jsonParam.toString());

                wr.flush();
                wr.close();

                Log.e("code",conn.getResponseCode()+"");

                if (conn.getResponseCode() == 404) {
                    // no messages to get
                    return null;
                }
                inputStream = conn.getInputStream();

                conn.disconnect();

            } catch (MalformedURLException e) {
                Log.e("MalformedURLException", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            } catch (JSONException e) {
                Log.e("JSONExcept", e.getMessage());
            }


            // read content into string
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                inputStream.close();
                json = stringBuilder.toString();
            } catch (UnsupportedEncodingException e) {
                //Log.e("UnsupportedEncoding", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            }

            // create jsonArray from string
            if (json != "") {
                try {
                    jsonArray = new JSONArray(json);
                } catch (JSONException e) {
                    Log.e("JSONException", e.getMessage());
                }
            }

            return jsonArray;
        }


        public void populateList(JSONArray jsonArray) {
            // parse jsonArray into eventData objects
            if (jsonArray == null) {
                return;
            }
            try{
                event.clearForumList();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String username = jsonObject.getString("username");
                    String date = jsonObject.getString("date");
                    String message = jsonObject.getString("message");

                    Calendar time = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                    try {
                        time.setTime(sdf.parse(date));// all done
                    }
                    catch (ParseException e) {
                        Log.e("ParseExcept",e.getMessage());
                    }

                    ForumPost forumPost = new ForumPost(username,message,time);
                    event.addMessageToForum(forumPost);
                }

                forumListAdapter.notifyDataSetChanged();
                backToDefaultForumConfig();
                ((MyApplication) getApplication()).setTempEvent(event);
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
            }
        }

    }
}
