package io.github.vrchatapi.mobile.API;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class VRCNotification {

	private static final SimpleDateFormat AFTER_DATE_FORMAT = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	
	protected String id;
	protected String senderUserId;
	protected String receiverUserId;
	protected String senderUsername;
	protected NotificationType notificationType;
	protected String message;
	protected JSONObject details;
	protected boolean seen;

	protected VRCNotification() {
	}

	public void Init(JSONObject json) {
		try {
			id = json.getString("id");
			senderUserId = json.getString("senderUserId");
			receiverUserId = json.optString("receiverUserId", null);
			senderUsername = json.optString("senderUsername", null);
			notificationType = NotificationType.fromString(json.getString("type"));
			message = json.optString("message", null);
			seen = json.optBoolean("seen", false);
			details = json.optJSONObject("details");
		}
		catch(Exception e){
			Log.i("VRCNotification", "init - " + e.getMessage());
		}
	}

	public static VRCNotification sendNotification(VRCUser target, NotificationType type, String message,
			JSONObject details) {
		Map<String, Object> dic = new HashMap<>();
		dic.put("type", type.toString());
		dic.put("message", message);
		dic.put("details", details.toString());
		JSONObject resp = ApiModel.sendPostRequest("user/" + target.id + "/notification", dic);
		VRCNotification notification = new VRCNotification();
		notification.Init(resp);
		return notification;
	}
	
	public static List<VRCNotification> fetchAll(NotificationType type, boolean sent, Date after) {
		try {
			Map<String, Object> dic = new HashMap<>();
			if (type != NotificationType.ALL) {
				if (type == NotificationType.FRIEND_REQUEST) {
					dic.put("type", "friendRequest");
				} else {
					dic.put("type", type.toString());
				}
			}
			if (sent) {
				dic.put("sent", true);
			}
			if (after != null) {
				dic.put("after", AFTER_DATE_FORMAT.format(after));
			}
			JSONArray obj = ApiModel.sendGetRequestArray("auth/user/notifications", dic);
			List<VRCNotification> list = new ArrayList<>();
			for (int i = 0; i < obj.length(); i++) {
				Object current = obj.get(i);
				if (current instanceof JSONObject) {
					JSONObject not = (JSONObject) current;
					VRCNotification notification = new VRCNotification();
					notification.Init(not);
					list.add(notification);
				}
			}
			return list;
		}
		catch(Exception e){
			Log.i("VRCNotification", "fetchAll - " + e.getMessage());
			return null;
		}
	}
	
	public void accept() {
		JSONObject obj = ApiModel.sendPutRequest("auth/user/notifications/" + id + "/accept", null);
		System.out.println(obj);
	}

	public boolean wasSeen() {
		return seen;
	}

	public String getMessage() {
		return message;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public VRCUser getSender() {
		if(senderUserId == null) return null;
		return VRCUser.fetch(senderUserId);
	}

	public String getSenderUsername() {
		return senderUsername;
	}
	
	public VRCUser getReceiver() {
		if(receiverUserId == null) return null;
		return VRCUser.fetch(receiverUserId);
	}

	public void markAsRead() {
		JSONObject resp = ApiModel.sendPutRequest("auth/user/notifications/" + id + "/see", null);
		Init(resp);
	}
	
	public void delete() {
		JSONObject resp = ApiModel.sendPutRequest("auth/user/notifications/" + id + "/hide", null);
		// TODO: What should we do here?
		// will have to test 
		Init(resp);
	}

	@Override
	public String toString() {
		return "VRCNotification [Sender: " + senderUsername + ", Type: " + notificationType + ", message: " + message + ", details=" + details + ", seen=" + seen + "]";
	}

}
