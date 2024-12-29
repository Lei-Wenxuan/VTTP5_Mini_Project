package sg.edu.nus.iss.vttp5_mini_project.model;

import java.util.Date;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class VideoInfo {

    private String videoId;
    private String title;
    private String thumbnailUrl;
    private String description;
    private Date publishedDate;
    private String prettyDate;
    private String videoDuration;
    private String categoryId;
    private String playerEmbedHtml;
    private String keyword;
    private ChannelInfo channelInfo;
    private VideoStatistics videoStatistics;

    public VideoInfo() {
    }

    public VideoInfo(String videoId, String title, String thumbnailUrl, String description, Date publishedDate,
            String prettyDate, String videoDuration, String categoryId, String playerEmbedHtml, String keyword,
            ChannelInfo channelInfo, VideoStatistics videoStatistics) {
        this.videoId = videoId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.publishedDate = publishedDate;
        this.prettyDate = prettyDate;
        this.videoDuration = videoDuration;
        this.categoryId = categoryId;
        this.playerEmbedHtml = playerEmbedHtml;
        this.keyword = keyword;
        this.channelInfo = channelInfo;
        this.videoStatistics = videoStatistics;
    }
    
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getPublishedDate() { return publishedDate; }
    public void setPublishedDate(Date publishedDate) { this.publishedDate = publishedDate; }

    public String getPrettyDate() { return prettyDate; }
    public void setPrettyDate(String prettyDate) { this.prettyDate = prettyDate; }

    public String getVideoDuration() { return videoDuration; }
    public void setVideoDuration(String videoDuration) { this.videoDuration = videoDuration; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getPlayerEmbedHtml() { return playerEmbedHtml; }
    public void setPlayerEmbedHtml(String playerEmbedHtml) { this.playerEmbedHtml = playerEmbedHtml; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public ChannelInfo getChannelInfo() { return channelInfo; }
    public void setChannelInfo(ChannelInfo channelInfo) { this.channelInfo = channelInfo; }

    public VideoStatistics getVideoStatistics() { return videoStatistics; }
    public void setVideoStatistics(VideoStatistics videoStatistics) { this.videoStatistics = videoStatistics; }

    public JsonObject toJson() {
        JsonObject jObject = Json
                .createObjectBuilder()
                .add("videoId", this.videoId)
                .add("title", this.title)
                .add("thumbnailUrl", this.thumbnailUrl)
                .add("description", this.description)
                .add("publishedDate", this.publishedDate.toString())
                .add("prettyDate", this.prettyDate)
                .add("videoDuration", this.videoDuration)
                .add("categoryId", this.categoryId)
                .add("playerEmbedHtml", this.playerEmbedHtml)
                .add("keyword", this.keyword)
                .add("channelInfo", this.channelInfo.toJson().toString())
                .add("videoStatistics", this.videoStatistics.toJson().toString())
                .build();

        return jObject;
    }

}