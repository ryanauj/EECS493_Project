package otk.test;

/**
 * Created by Tim on 11/21/2015.
 */
public class UserData {

    private String userName;
    private int color = 0xffecd029; //Color is 0xAARRGGBB where AA is alpha (transparency)

    UserData(String name) {
        this.userName = name;
    }

    UserData(UserData user) {
        this.userName = user.getUserName();
    }

    public void setUserName(String name)
    {
        userName = name;
    }
    public String getUserName()
    {
        return userName;
    }

    public void setColor(int newColor)
    {
        this.color = newColor;
    }

    public int getColor()
    {
        return this.color;
    }
}
