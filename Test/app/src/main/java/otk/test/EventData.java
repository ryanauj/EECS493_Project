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

    EventData(){}

    EventData(String creator, String title, String description, String pictureUrl, MarkerOptions location, Date time) {
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.location = location;
        this.time = time;
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

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String dataToString() {
        return "title:" + this.title
                + "\ncreator:" + this.creator
                + "\ndescription:" + this.description
                + "\nlocation:" + this.location.toString()
                + "\ndatetime:" + this.time.toString()
                + "\nimagepath" + this.pictureUrl;
    }

}
