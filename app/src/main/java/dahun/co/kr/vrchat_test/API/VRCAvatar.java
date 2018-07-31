package dahun.co.kr.vrchat_test.API;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class VRCAvatar {
	
	private String id;
	private String name;
	private String imageUrl;
	private String authorName;
	private String authorId;
	private String assetUrl;
	private String description;
	private ReleaseStatus releaseStatus;
	private List<String> tags = new ArrayList<>();
	private int version;
	private String thumbnailImageUrl;
	
	protected VRCAvatar() {}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public VRCUser getAuthor() {
		return VRCUser.fetch(authorId);
	}
	
	public String getAuthorName() {
		return authorName;
	}
	
	public String getAssetUrl() {
		return assetUrl;
	}
	
	public String getDescription() {
		return description;
	}
	
	public ReleaseStatus getReleaseStatus() {
		return releaseStatus;
	}
	
	public String getThumbnailImageUrl() {
		return thumbnailImageUrl;
	}
	
	public int getVersion() {
		return version;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
	public void init(JSONObject json) {
		try {
			this.id = json.getString("id");
			this.name = json.getString("name");
			this.imageUrl = json.getString("imageUrl");
			this.authorName = json.getString("authorName");
			this.authorId = json.getString("authorId");
			this.assetUrl = json.getString("assetUrl");
			this.description = json.getString("description");
			this.releaseStatus = ReleaseStatus.valueOf(json.getString("releaseStatus").toUpperCase());
			JSONArray arr = json.getJSONArray("tags");
			for (int i=0; i<arr.length(); i++) {
				Object obj = arr.get(i);
				tags.add(obj.toString());
			}
			this.thumbnailImageUrl = json.optString("thumbnailImageUrl", null);
		}
		catch(Exception e){
			Log.i("VRCAvatar", "init - " + e.getMessage());
		}
	}
	
	public static VRCAvatar fetch(String id) {
		JSONObject obj = ApiModel.sendGetRequest("avatars/" + id, null);
		VRCAvatar avatar = new VRCAvatar();
		avatar.init(obj);
		return avatar;
	}
	
	public void select() {
		ApiModel.sendPutRequest("avatars/" + id + "/select	", null);
	}
	
	public void delete() {
		ApiModel.sendRequest("avatars/" + id, "DELETE", null);
	}
	
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		
		return object;
	}
	
}
