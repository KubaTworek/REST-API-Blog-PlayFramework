package comments.model;

public class CommentResource {
    private String id;
    private String content;
    private String link;

    public CommentResource() {
    }

    public CommentResource(String id, String content, String link) {
        this.id = id;
        this.content = content;
        this.link = link;
    }

    public CommentResource(Comment comment, String link) {
        this.id = comment.id.toString();
        this.content = comment.content;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getLink() {
        return link;
    }
}