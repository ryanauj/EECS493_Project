package otk.test;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rj on 12/9/15.
 */
public class ForumPost {
    private String user, message;
    private Calendar time;

    public ForumPost(String user, String message, Calendar time) {
        this.user = user;
        this.message = message;
        this.time = (Calendar) time.clone();
    }

    public String getUser() { return user; }
    public String getMessage() { return message; }
    public Calendar getTime() { return time; }
}
