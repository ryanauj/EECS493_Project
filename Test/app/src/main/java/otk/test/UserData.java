package otk.test;

/**
 * Created by Tim on 11/21/2015.
 */
public class UserData {

    private String userName;
    private int colorValue; //Color is 0xAARRGGBB where AA is alpha (transparency)

    UserData(String name,int colorValue) {
        this.userName = name;
        this.colorValue = colorValue;
    }

    UserData(String name) {
        this.userName = name;
        this.colorValue = 0xff59a6a6;
    }

    UserData(UserData user) {
        this.userName = user.getUserName();
        this.colorValue = user.getColorValue();
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getUserName() {
        return userName;
    }


    public void setColorValue(int newColor) {
        this.colorValue = newColor;
    }

    public int getColorValue() {
        return this.colorValue;
    }
}
