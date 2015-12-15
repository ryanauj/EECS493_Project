package otk.test;

import android.app.Application;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tim on 11/25/2015.
 */
public class MyApplication extends Application {
    private List<EventData> eventStorage = new LinkedList<>();
    private UserData currUser = new UserData("App Opened",R.color.black);
    private EventData tempEvent;


    public void setTempEvent(EventData event)
    {
        tempEvent = event;
    }

    public EventData getTempEvent()
    {
        return tempEvent;
    }

    public void setUser(UserData user)
    {
        this.currUser = user;
    }

    public UserData getUser()
    {
        return this.currUser;
    }

    public void addToEventList(EventData add)
    {
        eventStorage.add(0,add);
    }

    public void clearEventStorage() {
        this.eventStorage = new LinkedList<>();
    }

    public List<EventData> getEventStorage() { return eventStorage; }

    public void logEventList() {
        for (int i = 0; i < eventStorage.size(); i++) {
            String event = "event" + i;
            Log.e(event,eventStorage.get(i).dataToString());
        }
    }

}
