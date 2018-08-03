package io.github.vrchatapi.mobile.API;

public enum NotificationType {
	
	ALL,
	MESSAGE,
	FRIEND_REQUEST,
	INVITE,
	VOTE_TO_KICK,
	HALP,
	HIDDEN,
	;
	
	public static NotificationType fromString(String str) {
		for(NotificationType type : NotificationType.values()) {
			if(type.toString().equalsIgnoreCase(str)) return type;
		}
		return null;
	}
	
	@Override
	public String toString() {
		if(this == FRIEND_REQUEST) return "friendRequest";
		return super.toString().toLowerCase().replace("_", "");
	}
	
}
