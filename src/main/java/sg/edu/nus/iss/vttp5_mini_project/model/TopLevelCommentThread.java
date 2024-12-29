package sg.edu.nus.iss.vttp5_mini_project.model;

public class TopLevelCommentThread {
    
    private String topLevelComment;
    private String authorName;
    private String authorImageUrl;
    
    public TopLevelCommentThread() {
    }

    public TopLevelCommentThread(String topLevelComment, String authorName, String authorImageUrl) {
        this.topLevelComment = topLevelComment;
        this.authorName = authorName;
        this.authorImageUrl = authorImageUrl;
    }

    public String getTopLevelComment() { return topLevelComment; }
    public void setTopLevelComment(String topLevelComment) { this.topLevelComment = topLevelComment; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorImageUrl() { return authorImageUrl; }
    public void setAuthorImageUrl(String authorImageUrl) { this.authorImageUrl = authorImageUrl; }
    
}
