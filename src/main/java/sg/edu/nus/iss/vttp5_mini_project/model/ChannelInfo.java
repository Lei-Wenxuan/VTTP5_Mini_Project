package sg.edu.nus.iss.vttp5_mini_project.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class ChannelInfo {

    private String channelId;
    private String name;
    private String thumbnailUrl;
    private long subscriptionCount;

    public ChannelInfo() {
    }

    public ChannelInfo(String channelId, String name, String thumbnailUrl, long subscriptionCount) {
        this.channelId = channelId;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.subscriptionCount = subscriptionCount;
    }

    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    
    public long getSubscriptionCount() { return subscriptionCount; }
    public void setSubscriptionCount(long subscriptionCount) { this.subscriptionCount = subscriptionCount; }

    public JsonObject toJson() {
        JsonObject jObject = Json
                .createObjectBuilder()
                .add("channelId", this.channelId)
                .add("name", this.name)
                .add("thumbnailUrl", this.thumbnailUrl)
                .add("subscriptionCount", this.subscriptionCount)
                .build();

        return jObject;
    }

}
