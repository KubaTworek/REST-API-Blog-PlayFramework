package comments.model;

import posts.model.Post;
import users.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.REMOVE;

@Entity
@Table(name="Comment")
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;
    public String content;
    @ManyToOne(fetch=FetchType.EAGER, cascade = { REMOVE, ALL })
    @JoinColumn(name="user_id")
    public User user;
    @ManyToMany(fetch=FetchType.EAGER, cascade = { REMOVE, ALL })
    @JoinTable(
            name="PostComment",
            joinColumns = @JoinColumn(name="comment_id"),
            inverseJoinColumns = @JoinColumn(name="post_id")
    )
    private List<Post> posts;

    public Comment() {
    }

    public Comment(String content) {
        this.content = content;
    }

    public Comment(String content, User user, Post post) {
        this.content = content;
        user.add(this);
        post.add(this);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void add(Post tempPost) {
        if(posts == null) {
            posts = new ArrayList<>();
        }

        posts.add(tempPost);
        tempPost.add(this);
    }
}
