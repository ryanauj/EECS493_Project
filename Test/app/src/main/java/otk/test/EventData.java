package otk.test;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by Andrew on 11/14/2015.
 */
public class EventData {
    private String creator, title, description, nameOfLocation, pictureUrl;
    private LatLng location;
    private Date time;

    EventData(String creator, String title, String description, String pictureUrl, LatLng location, Date time) {
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.location = location;
        this.time = time;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(LatLng location) {
        this.location = location;
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

    public LatLng getLocation() {
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
}
