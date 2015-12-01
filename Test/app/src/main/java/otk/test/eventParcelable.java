package otk.test;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.jar.Attributes;

/**
 * Created by Tim on 11/25/2015.
 */
public class eventParcelable implements Parcelable {
    private double Latitude, Longitude;
    private String creator, title, description, nameOfLocation;

    eventParcelable(double latitude, double longitude, String create, String Title, String Description, String NameOfLocation)
    {
        Latitude = latitude;
        Longitude = longitude;
        creator = create;
        title = Title;
        description = Description;
        nameOfLocation = NameOfLocation;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(Latitude);
        out.writeDouble(Longitude);
        out.writeString(creator);
        out.writeString(title);
        out.writeString(description);
        out.writeString(nameOfLocation);

    }

    public static final Parcelable.Creator<eventParcelable> CREATOR
            = new Parcelable.Creator<eventParcelable>() {
        public eventParcelable createFromParcel(Parcel in) {
            return new eventParcelable(in);
        }

        public eventParcelable[] newArray(int size) {
            return new eventParcelable[size];
        }
    };

    private eventParcelable(Parcel in) {
        Latitude = in.readDouble();
        Longitude = in.readDouble();
        creator = in.readString();
        title = in.readString();
        description = in.readString();
        nameOfLocation = in.readString();
    }
}
