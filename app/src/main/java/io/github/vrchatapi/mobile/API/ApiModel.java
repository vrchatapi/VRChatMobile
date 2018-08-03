package io.github.vrchatapi.mobile.API;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiModel {
	
	private static final String API_URL = "https://api.vrchat.cloud/api/1/";
	protected static String apiKey;
	
	static {
		// TODO: Maybe move this to somewhere else?
		fetchApiKey();
	}
	
	private static void fetchApiKey() {
		if(!VRCRemoteConfig.isInitialized()) {
			VRCRemoteConfig.init();
		}
		if(VRCRemoteConfig.isInitialized()) {
			apiKey = VRCRemoteConfig.getString("clientApiKey");
			if(apiKey == null || apiKey.isEmpty()) {
				Log.i("ApiModel", "Could not fetch client api key - unknown error.");
			}
		}else {
			Log.i("ApiModel", "Could not fetch client api key - config not initialized");
		}
	}
	
	public static JSONObject sendGetRequest(String endpoint, Map<String, Object> requestParams) {
		return sendRequest(endpoint, "get", requestParams);
	}
	
	public static JSONObject sendPostRequest(String endpoint, Map<String, Object> requestParams) {
		return sendRequest(endpoint, "post", requestParams);
	}

	public static JSONObject sendPutRequest(String endpoint, Map<String, Object> requestParams) {
		return sendRequest(endpoint, "put", requestParams);
	}
	
	protected static JSONObject sendRequest(String endpoint, String method, Map<String, Object> requestParams) {
		if(requestParams != null && requestParams.size() == 0) requestParams = null;

		JSONObject resp = null;
		String apiUrl = getApiUrl();
		String uri = apiUrl + endpoint;
		boolean hasOne = false;
		if(apiKey != null) {
			uri += "?apiKey=" + apiKey;
			hasOne = true;
		}
		String requestText = "";
		if(requestParams != null) {
			if(method.equalsIgnoreCase("get")) {
				uri += (hasOne ? "&" : "?") + HttpUtil.urlEncode(requestParams);
			}else {
				requestText = new JSONObject(requestParams).toString();
			}
		}
		Log.i("ApiModel", "Sending " + method + " request to " + uri);
		try {
			URL url = new URL(uri);
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			conn.setRequestMethod(method.toUpperCase());
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			conn.setRequestProperty("Content-Type", (requestText.isEmpty()) ? "application/x-www-form-urlencoded" : "application/json");
			conn.setRequestProperty("user-agent", "VRChatJava");
			if(VRCCredentials.getWebCredentials() != null) {
				conn.setRequestProperty("Authorization", VRCCredentials.getWebCredentials());
			}
			if(VRCCredentials.getAuthToken() != null) {
				conn.setRequestProperty("Cookie", "auth=" + VRCCredentials.getAuthToken());
			}
			if(!requestText.isEmpty()) {
				conn.setDoOutput(true);
				conn.getOutputStream().write(requestText.getBytes());
			}
			StringBuilder result = new StringBuilder();
			BufferedReader rd = null;
			if(conn.getResponseCode() != 200) {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}else {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			}
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			Log.i("ApiModel", result.toString());
			resp = new JSONObject(result.toString());
			if(conn.getResponseCode() != 200) {
				String error = new JSONObject(result.toString()).optString("error");
				if(error == null || error.isEmpty()) {
					JSONObject errObj = new JSONObject(result.toString()).optJSONObject("error");						
					if(errObj != null) {
						error = errObj.optString("message", "unknown");
					}else {
						error = "unknown";
					}
				}
				Log.i("ApiModel", "Error sending request - " + error);
				throw new VRCException(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
	

	public static JSONArray sendGetRequestArray(String endpoint, Map<String, Object> requestParams) {
		return sendRequestArray(endpoint, "get", requestParams);
	}
	
	public static JSONArray sendPostRequestArray(String endpoint, Map<String, Object> requestParams) {
		return sendRequestArray(endpoint, "post", requestParams);
	}

	public static JSONArray sendPutRequestArray(String endpoint, Map<String, Object> requestParams) {
		return sendRequestArray(endpoint, "put", requestParams);
	}
	
	protected static JSONArray sendRequestArray(String endpoint, String method, Map<String, Object> requestParams) {
		if(requestParams != null && requestParams.size() == 0) requestParams = null;
		
		JSONArray resp = null;
		String apiUrl = getApiUrl();
		String uri = apiUrl + endpoint;
		if(apiKey != null) {
			uri += (uri.contains("?") ? "&" : "?") + "apiKey=" + apiKey;
		}
		String requestText = "";
		if(requestParams != null) {
			if(method.equalsIgnoreCase("get")) {
				uri += (uri.contains("?") ? "&" : "?") + HttpUtil.urlEncode(requestParams);
			}else {
				requestText = new JSONObject(requestParams).toString();
			}
		}
		Log.i("ApiModel", "Sending " + method + " request to " + uri);
		try {
			URL url = new URL(uri);
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			conn.setRequestMethod(method.toUpperCase());
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			conn.setRequestProperty("Content-Type", (requestText.isEmpty()) ? "application/x-www-form-urlencoded" : "application/json");
			conn.setRequestProperty("user-agent", "VRChatJava");
			if(VRCCredentials.getWebCredentials() != null) {
				conn.setRequestProperty("Authorization", VRCCredentials.getWebCredentials());
			}
			if(VRCCredentials.getAuthToken() != null) {
				conn.setRequestProperty("Cookie", "auth=" + VRCCredentials.getAuthToken());
			}
			if(!requestText.isEmpty()) {
				conn.setDoOutput(true);
				conn.getOutputStream().write(requestText.getBytes());
			}
			StringBuilder result = new StringBuilder();
			BufferedReader rd = null;
			if(conn.getResponseCode() != 200) {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}else {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			}
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			try {
				resp = new JSONArray(result.toString());
			}catch(Exception e) {
				if(conn.getResponseCode() != 200) {
					String error = new JSONObject(result.toString()).optString("error");
					if(error == null || error.isEmpty()) {
						JSONObject errObj = new JSONObject(result.toString()).optJSONObject("error");						
						if(errObj != null) {
							error = errObj.optString("message", "unknown");
						}else {
							error = "unknown";
						}
					}
					Log.i("ApiModel", "Error sending request - " + error);
					throw new VRCException(error);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
	
	public static String getApiUrl() {
		return API_URL;
	}
	
}
