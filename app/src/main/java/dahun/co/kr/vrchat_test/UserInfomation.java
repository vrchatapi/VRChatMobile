package dahun.co.kr.vrchat_test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.URL;

public class UserInfomation {
    private String imageURI;
    private String displayName;
    private String location;
    private Bitmap imageBitmap;

    UserInfomation(String imageURI, String displayName, String location, Bitmap imageBitmap){
        this.imageURI = imageURI;
        this.displayName = displayName;
        this.location = location;
        this.imageBitmap = imageBitmap;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImageURI() {
        return imageURI;
    }

    public String getLocation() {
        return location;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Bitmap getImageBitmap() { return imageBitmap; }

    public static Bitmap loadingBitmap(String imageURI){
        try {
            URL url = new URL(imageURI);
            InputStream is = url.openStream();
            return UserInfomationAdapter.getRoundedCornerBitmap(BitmapFactory.decodeStream(is), 100);
        } catch (Exception e) {
            return null;
        }
    }

}
