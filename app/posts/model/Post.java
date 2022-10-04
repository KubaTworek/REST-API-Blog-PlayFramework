package posts.model;

import comments.model.Comment;
import users.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Post")
public class Post {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;
    public String title;
    public String text;
    public Long timestamp;
    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    public User user;

    @ManyToMany(fetch=FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name="PostComment",
            joinColumns = @JoinColumn(name="post_id"),
            inverseJoinColumns = @JoinColumn(name="comment_id")
    )
    private List<Comment> comments;

    public Post() {
        this.timestamp = System.currentTimeMillis();
    }

    public Post(String title, String text) {
        this.title = title;
        this.text = text;
        this.timestamp = System.currentTimeMillis();
    }

    public Post(String title, String text, User user) {
        this.title = title;
        this.text = text;
        user.add(this);
        this.timestamp = System.currentTimeMillis();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void add(Comment tempComment) {
        if(comments == null) {
            comments = new ArrayList<>();
        }

        comments.add(tempComment);
        tempComment.add(this);
    }
}
