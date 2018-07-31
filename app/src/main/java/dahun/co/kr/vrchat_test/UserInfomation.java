package dahun.co.kr.vrchat_test;

public class UserInfomation {
    private String imageURI;
    private String displayName;
    private String location;

    UserInfomation(String imageURI, String displayName, String location){
        this.imageURI = imageURI;
        this.displayName = displayName;
        this.location = location;
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
}
