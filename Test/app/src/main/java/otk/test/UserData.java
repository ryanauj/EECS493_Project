package otk.test;

/**
 * Created by Tim on 11/21/2015.
 */
public class UserData {

    private String userName;
    private String color;

    UserData(String name) {
        this.userName = name;
    }

    UserData(UserData user) {
        this.userName = user.getUserName();
        this.color = user.getColor();
    }

    public void setUserName(String name)
    {
        this.userName = name;
    }
    public String getUserName()
    {
        return userName;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getColor() {
        return this.color;
    }
}
