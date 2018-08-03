package io.github.vrchatapi.mobile.API;

import android.util.Base64;

public class VRCCredentials {
	
	private static String username;
	private static String password;
	private static String webCredentials;
	private static String authToken;

    public static String getBase64encode(String content){
        return Base64.encodeToString(content.getBytes(), 0);
    }

	public static String getBase64decode(String content){

		return Base64.encode(content.getBytes(), 0).toString();
	}

	public static void setUser(String user, String pass) {
		VRCCredentials.username = user;
		VRCCredentials.password = pass;

		VRCCredentials.webCredentials = "basic " + getBase64encode(user + ":" + pass);
		// VRCCredentials.webCredentials = "basic " + java.util.Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());

        //Log.i("Testing", username + ":" + password + " = " + webCredentials);
        //Log.i("Testing", username + ":" + password + " = " + "basic " + getBase64encode(user + ":" + pass));
		VRCCredentials.authToken = null;
	}
	
	public static void clear() {
		VRCCredentials.username = null;
		VRCCredentials.password = null;
		VRCCredentials.webCredentials = null;
		VRCCredentials.authToken = null;		
	}
	
	public static void setAuthToken(String token) {
		VRCCredentials.username = null;
		VRCCredentials.password = null;
		VRCCredentials.webCredentials = null;
		VRCCredentials.authToken = token;		
	}
	
	public static String getWebCredentials() {
		return webCredentials;
	}
	
	public static String getAuthToken() {
		return authToken;
	}
	
	public static String getUsername() {
		return username;
	}
	
	protected static String getPassword() {
		return password;
	}
	
}
