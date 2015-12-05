package otk.test;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateEvent extends AppCompatActivity {

    public Calendar cal = Calendar.getInstance();
    private LatLng tempLoc;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        final EventData createEventData = ((MyApplication) this.getApplication()).getTempEvent();
        tempLoc = createEventData.getLocation().getPosition();

        final EditText titleView = (EditText) findViewById(R.id.title);
        final TextView creatorView = (TextView) findViewById(R.id.creator);
        final EditText descriptionView = (EditText) findViewById(R.id.description);

        //final EditText locationView = (EditText) findViewById(R.id.location);

        if(createEventData != null)
        {
            titleView.setText(createEventData.getTitle());
            creatorView.setText( ((MyApplication) getApplication()).getUser().getUserName());
            descriptionView.setText(createEventData.getDescription());
            //locationView.setText(createEventData.getLocation().getPosition().toString());
        }

        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(getTimeString(cal));
        TextView dateView = (TextView) findViewById(R.id.date);
        dateView.setText(getDateString(cal));

        final LinearLayout mainpage = (LinearLayout) findViewById(R.id.mainpage);
        mainpage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mainpage.requestFocus();
                hideSoftKeyboard(CreateEvent.this);
                return false;
            }
        });

        timeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mainpage.requestFocus();
                hideSoftKeyboard(CreateEvent.this);
                setTime();
                return false;
            }
        });

        dateView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mainpage.requestFocus();
                hideSoftKeyboard(CreateEvent.this);
                setDate();
                return false;
            }
        });

        Button createEvent = (Button) findViewById(R.id.createevent);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click", "submit clicked");
                Log.e("title", titleView.getText().toString());
                //Log.e("creator", creatorView.getText().toString());
                Log.e("description", descriptionView.getText().toString());
                //Log.e("location", locationView.getText().toString());

                Date returnDate = new Date(cal.getTime().getTime());
                //createEventData.setCreator(creatorView.getText().toString());
                createEventData.setCreator((((MyApplication) getApplication()).getUser().getUserName()));
                createEventData.setDescription(descriptionView.getText().toString());
                createEventData.setTime(returnDate);
                createEventData.setTitle(titleView.getText().toString());
                ((MyApplication) getApplication()).setTempEvent(createEventData);

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                //Log.e("time", timeView.getHour()+" "+timeView.getMinute()+timeView.getBaseline());
                //Log.e("date", dateView.getMonth() + " " + dateView.getDayOfMonth() + " " + dateView.getYear());
            }
        });

        final Button setTime = (Button) findViewById(R.id.timebutton);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainpage.requestFocus();
                hideSoftKeyboard(CreateEvent.this);
                setTime();
            }
        });

        final Button setDate = (Button) findViewById(R.id.datebutton);
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainpage.requestFocus();
                hideSoftKeyboard(CreateEvent.this);
                setDate();
            }
        });


        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapLocEditor);
        mapFragment.getMap().addMarker(new MarkerOptions().position(createEventData.getLocation().getPosition()).title("Event Location"));
        mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLng(createEventData.getLocation().getPosition()));
        mapFragment.getMap().animateCamera(CameraUpdateFactory.zoomTo(15));

        mapFragment.getMap().setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                //eventParcelable parcelToSend= new eventParcelable(point.latitude, point.longitude, "Tim", "TempParcel","Just a temporary thing","Home");
                ((MyApplication) getApplication()).getTempEvent().setLocation(point);
                mapFragment.getView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                mapFragment.getMap().clear();
                mapFragment.getMap().addMarker(new MarkerOptions().position(point).title("Event Location"));
                createEventData.setLocation(point);
                //final EditText locationView = (EditText) findViewById(R.id.location);
                //locationView.setText(point.toString());
            }
        });

    }

    public void setTime() {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                cal.set(Calendar.HOUR_OF_DAY,hourOfDay);
                cal.set(Calendar.MINUTE,minute);

                TextView timeView = (TextView) findViewById(R.id.time);
                timeView.setText(getTimeString(cal));
            }
        },cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),false).show();
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

    public void setDate() {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TextView dateView = (TextView) findViewById(R.id.date);
                dateView.setText(getDateString(cal));
            }
        },cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show();
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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
