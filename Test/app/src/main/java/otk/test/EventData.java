package otk.test;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

/**
 * Created by Andrew on 11/14/2015.
 */
public class EventData {
    private MarkerOptions location = new MarkerOptions();
    private String creator, title, description, nameOfLocation, pictureUrl;
    private Date time;
    private int max_attend,total_rsvp,color;

    EventData() {
        this.creator = "";
        this.title = "";
        this.description = "";
        this.pictureUrl = "";
        this.location = new MarkerOptions();
        this.location.position(new LatLng(0,0));
        this.time = new Date();
        this.color = 0xffffffff;//Hex for black
    }

    EventData(String creator, String title, String description, String pictureUrl, MarkerOptions location, Date time) {
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.location = location;
        this.time = time;
    }

    EventData(String creator, String title, String description, MarkerOptions location, Date time, int color) {
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.location = location;
        this.time = time;
        this.color = color;
    }

    EventData(EventData replicateEvent)
    {
        this.creator=replicateEvent.getCreator();
        this.description = replicateEvent.getDescription();
        this.location = replicateEvent.getLocation();
        this.nameOfLocation = replicateEvent.getNameOfLocation();
        this.time = replicateEvent.getTime();
        this.title = replicateEvent.getTitle();
        this.location = replicateEvent.getLocation();
        this.color = replicateEvent.getColor();
    }

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

    public void setTime(Date time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTime() {
        return time;
    }

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
