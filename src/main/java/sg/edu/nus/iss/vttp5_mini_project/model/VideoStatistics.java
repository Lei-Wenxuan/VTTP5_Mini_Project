package sg.edu.nus.iss.vttp5_mini_project.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class VideoStatistics {

    private long likeCount;
    private long dislikeCount;
    private long viewCount;
    private long commentCount;
    private String videoId;
    
    public VideoStatistics() {
    }

    public VideoStatistics(long likeCount, long dislikeCount, long viewCount, long commentCount, String videoId) {
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.videoId = videoId;
    }

    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }

    public long getDislikeCount() { return dislikeCount; }
    public void setDislikeCount(long dislikeCount) { this.dislikeCount = dislikeCount; }

    public long getViewCount() { return viewCount; }
    public void setViewCount(long viewCount) { this.viewCount = viewCount; }

    public long getCommentCount() { return commentCount; }
    public void setCommentCount(long commentCount) { this.commentCount = commentCount; }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public JsonObject toJson() {
        JsonObject jObject = Json
                .createObjectBuilder()
                .add("likeCount", this.likeCount)
                .add("dislikeCount", this.dislikeCount)
                .add("viewCount", this.viewCount)
                .add("commentCount", this.commentCount)
                .add("videoId", this.videoId)
                .build();

        return jObject;
    }

}
