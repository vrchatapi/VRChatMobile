package dahun.co.kr.vrchat_test.API;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


public class VRCUser {
	
	protected static VRCUser currentUser;

	public static boolean isLoggedIn() {
		return currentUser != null;
	}
	
	public static VRCUser getCurrentUser() {
		return currentUser;
	}
	
	public static void logout() {
		VRCCredentials.clear();
		currentUser = null;
	}
	
	public static void login() {
		JSONObject obj = ApiModel.sendGetRequest("auth/user", null);
		if(obj.has("error")) {
			Log.i("VRCUser","NOT Authenticated");
			return;
		}
		
		//VRCCredentials.setAuthToken(obj.getString("authToken"));
		
		VRCUser user = new VRCUser();
		user.init(obj);
		currentUser = user;
	}

	public static void login(String username, String password) {
		VRCCredentials.setUser(username, password);
		login();
	}
	
	public static VRCUser fetch(String id) {
		try {
			JSONObject obj = ApiModel.sendGetRequest("users/" + id, null);
			if (obj == null || !obj.has("id")) {
				Log.i("VRCUser","Invalid user record received.");
				return null;
			}
			VRCUser user = new VRCUser();
			user.initBrief(obj);
			return user;
		}
		catch(Exception e){
			Log.i("VRCUser", "fetch - " + e.getMessage());
			return null;
		}
	}
	
	protected String id;
	protected String displayName;
	protected String username;
	protected String password;
	protected String avatarId;
	protected boolean verified;
	protected boolean hasEmail;
	protected boolean hasBirthday;
	protected int acceptedTOSVersion;
	protected DeveloperType developerType;
	protected String location;
	protected List<String> friends = new ArrayList<>();
	protected String currentAvatarImageUrl;
	protected String currentAvatarThumbnailImageUrl;
	
	protected VRCUser() {}
	
	public void init(JSONObject json) {
		try {
			this.id = json.getString("id");
			this.username = json.getString("username");
			this.displayName = json.getString("displayName");
			this.verified = json.getBoolean("emailVerified");
			this.hasEmail = json.optBoolean("hasEmail", false);
			this.hasBirthday = json.optBoolean("hasBirthday", false);
			this.acceptedTOSVersion = json.optInt("acceptedTOSVersion", 0);
			this.avatarId = json.optString("currentAvatar", null);
			this.developerType = DeveloperType.valueOf(json.optString("developerType", "none").toUpperCase());

			//this.location = json.getString("location");

			JSONArray arr = json.optJSONArray("friends");
			if (arr != null){
				//arr.forEach((obj) -> friends.add(obj.toString()));
				for (int i = 0; i < arr.length(); i++) {
					Object obj = arr.get(i);
					friends.add(obj.toString());
				}
			}
		}
		catch(Exception e){
			Log.i("VRCUser", "init - " + e.getMessage());
		}
	}
	
	public void initBrief(JSONObject json) {
		try {
			this.id = json.getString("id");
			this.username = json.getString("username");
			this.displayName = json.getString("displayName");
			this.avatarId = json.optString("location", null);

			this.location = json.getString("location");
			//this.location = json.getString("worldId");

			this.currentAvatarImageUrl = json.optString("currentAvatarImageUrl", null);
			this.currentAvatarThumbnailImageUrl = json.optString("currentAvatarThumbnailImageUrl", null);
			this.developerType = DeveloperType.valueOf(json.optString("developerType", "none").toUpperCase());
		}
		catch(Exception e){
			Log.i("VRCUser", "initBrief - " + e.getMessage());
		}
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getAvatarId() {
		return avatarId;
	}
	
	public boolean isVerified() {
		return verified;
	}
	
	public boolean hasBirthday() {
		return hasBirthday;
	}
	
	public boolean hasEmail() {
		return hasEmail;
	}
	
	public int getAcceptedTOSVersion() {
		return acceptedTOSVersion;
	}
	
	public DeveloperType getDeveloperType() {
		return developerType;
	}

	public String getCurrentAvatarImageUrl() { return currentAvatarImageUrl; }

	public String getCurrentAvatarThumbnailImageUrl() {	return currentAvatarThumbnailImageUrl; }

	public String getLocation() {
		return location;
	}
	
	public boolean hasModerationPowers() {
		return developerType == DeveloperType.MODERATOR || developerType == DeveloperType.INTERNAL;
	}
	
	public boolean isFriend() {
		return getCurrentUser().friends.contains(id);
	}
	
	@Override
	public String toString() {
		return "VRCUser [Username: " + username + ", Displayname: " + displayName + "]";
	}
	
}
