package dahun.co.kr.vrchat_test.API;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;



public class VRCRemoteConfig {
	
	private static JSONObject config;
	
	public static void init() {
		Log.i("VRCRemoteConfig","Fetching fresh config");
		JSONObject obj = ApiModel.sendGetRequest("config", null);
		//Log.ASSERT(!obj.has("error"), "could not fetch fresh config file.");
		config = obj;
	}
	
	public static boolean hasKey(String key) {
		if(!isInitialized()) {
			init();
		}
		if(isInitialized()) {
			return config.has(key);
		}
		return false;
	}
	
	public static String getString(String key) {
		if(!isInitialized()) {
			init();
		}
		if(isInitialized()) {
			return config.optString(key, null);
		}
		return null;
	}
	
	public static List<String> getList(String key) {
		try {
			if (!isInitialized()) {
				init();
			}
			List<String> result = new ArrayList<String>();
			if (isInitialized()) {
				JSONArray value = config.optJSONArray(key);
				if (value != null) {
					//value.forEach((obj) -> result.add(obj.toString()));
					//위의 람다식 -> 밑의 포문 (역할 같음. 람다식을 못쓰길래 포문으로 함)
					for (int i = 0; i < value.length(); i++) {
						Object obj = value.get(i);
						result.add(obj.toString());
					}
				}
			}
			return result;
		}
		catch(Exception e){
			Log.i("VRCRemoteConfig", "getList - " + e.getMessage());
			return null;
		}
	}
	
	public static boolean isInitialized() {
		return config != null;
	}
	
	private VRCRemoteConfig() {}
	
}
