package sg.edu.nus.iss.vttp5_mini_project.model;

import jakarta.validation.constraints.NotBlank;

public class CommentInfo {
    
    private String videoId;

    @NotBlank(message = "Comment text is mandatory")
    private String comment;

    public CommentInfo() {
    }

    public CommentInfo(String videoId, String comment) {
        this.videoId = videoId;
        this.comment = comment;
    }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    @Override
    public String toString() {
        return "CommentInfo [videoId=" + videoId + ", comment=" + comment + "]";
    }

}
