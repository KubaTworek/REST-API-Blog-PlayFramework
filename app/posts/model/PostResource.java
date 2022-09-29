package posts.model;

public class PostResource {
    private String id;
    private String title;
    private String text;
    private String link;

    public PostResource() {
    }

    public PostResource(String id, String title, String text, String link) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.link = link;
    }

    public PostResource(Post post, String link) {
        this.id = post.id.toString();
        this.title = post.title;
        this.text = post.text;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getLink() {
        return link;
    }
}
