package comments.model;

import posts.model.Post;
import users.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Comment")
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;
    public String content;
    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    public User user;
    @ManyToMany(fetch=FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
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
