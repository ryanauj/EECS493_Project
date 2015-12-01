package otk.test;

import android.app.Application;

import java.util.List;

/**
 * Created by Tim on 11/25/2015.
 */
public class MyApplication extends Application {
    private List<EventData> eventStorage;
    private UserData currUser;
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
        currUser = user;
    }

    public UserData getUser()
    {
        return currUser;
    }

    public void addToEventList(EventData add)
    {
        eventStorage.add(add);
    }

}
