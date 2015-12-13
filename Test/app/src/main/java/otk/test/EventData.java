package otk.test;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by Andrew on 11/14/2015.
 */
public class EventData {
    private MarkerOptions location = new MarkerOptions();
    private String id, creator, title, description, nameOfLocation, pictureUrl;
    private Calendar time, endTime;
    private int max_attend, color;
    private HashSet attendees;
    private LinkedList<ForumPost> forum_list;

    EventData() {
        this.id = "";
        this.creator = "";
        this.title = "";
        this.description = "";
        this.pictureUrl = "";
        this.location = new MarkerOptions();
        this.location.position(new LatLng(0,0));
        this.time = Calendar.getInstance();
        this.endTime = Calendar.getInstance();
        this.max_attend = 0;
        this.attendees = new HashSet();
        this.forum_list = new LinkedList<>();
        this.color = 0xffffffff;//Hex for black
    }

    EventData(String id, String creator, String title, String description, MarkerOptions location, Calendar time, Calendar endTime, int max_attend, int color, HashSet attendees, LinkedList<ForumPost> forum_list) {
        this.id = id;
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.location = location;
        this.time = time;
        this.endTime = endTime;
        this.max_attend = max_attend;
        this.attendees = attendees;
        this.forum_list = forum_list;
        this.color = color;
    }

    EventData(EventData replicateEvent)
    {
        this.id = replicateEvent.getId();
        this.creator = replicateEvent.getCreator();
        this.description = replicateEvent.getDescription();
        this.location = replicateEvent.getLocation();
        this.nameOfLocation = replicateEvent.getNameOfLocation();
        this.time = replicateEvent.getTime();
        this.endTime = replicateEvent.getEndTime();
        this.title = replicateEvent.getTitle();
        this.location = replicateEvent.getLocation();
        this.color = replicateEvent.getColor();
        this.max_attend = replicateEvent.getMaxAttendees();
        this.attendees = replicateEvent.getAttendees();
        this.forum_list = replicateEvent.getForumList();
    }

    public void setId(String id) { this.id = id; }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(LatLng location) {
        this.location.position(location);
    }

    public void setNameOfLocation(String nameOfLocation) {
        this.nameOfLocation = nameOfLocation;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public void setEndTime(Calendar endTime) { this.endTime = endTime; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMaxAttendees(int max) { this.max_attend = max; }

    public void addAttendee(String attendee) { this.attendees.add(attendee); }

    public void addMessageToForum(String user, String message) {
        forum_list.addFirst(new ForumPost(user, message));
    }

    public String getId() { return id; }

    public Calendar getTime() { return time; }

    public Calendar getEndTime() { return endTime; }

    public String getTitle() {
        return title;
    }

    public MarkerOptions getLocation() {
        return location;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public String getNameOfLocation() {
        return nameOfLocation;
    }

    public void setColor(int color) {
        this.color=color;
    }

    public int getColor() {
        return color;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public int getMaxAttendees() { return max_attend; }

    public HashSet getAttendees() { return attendees; }

    public int getNumAttendees() {
        if(attendees==null)
            return 0;
        else
            return attendees.size();
    }

    public LinkedList<ForumPost> getForumList() { return forum_list; }

    public String dataToString() {
        return "title:" + this.title
                + "\ncreator:" + this.creator
                + "\ndescription:" + this.description
                + "\nlocation:" + this.location.toString()
                + "\ndatetime:" + this.time.toString();
    }

    @Override
    public String toString() {
        return title;
    }

}
