package otk.test;

/**
 * Created by Tim on 11/21/2015.
 */
public class UserData {

    private String userName;

    UserData(String name) {
        this.userName = name;
    }

    public void setUserName(String name)
    {
        userName = name;
    }
    public String getUserName()
    {
        return userName;
    }
}
