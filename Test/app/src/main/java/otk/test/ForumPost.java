package otk.test;

import java.util.Date;

/**
 * Created by rj on 12/9/15.
 */
public class ForumPost {
    private String user, message;
    private Date date;

    public ForumPost(String user, String message) {
        this.user = user;
        this.message = message;
        this.date = new Date();
    }

    public String getUser() { return user; }
    public String getMessage() { return message; }
    public Date getDate() { return date; }
}
