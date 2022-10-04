package users.model;

import comments.model.Comment;
import posts.model.Post;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.REMOVE;

@Entity
@Table(name="User")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;
    public String firstName;
    public String lastName;

    @OneToMany(fetch=FetchType.EAGER, cascade = { REMOVE, ALL })
    @JoinColumn(name = "user_id")
    public List<Post> posts;

    @OneToMany(cascade = { REMOVE, ALL })
    @JoinColumn(name = "user_id")
    public List<Comment> comments;

    public User() {
    }

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void add(Post tempPost) {
        if(posts == null) {
            posts = new ArrayList<>();
        }

        posts.add(tempPost);
        tempPost.setUser(this);
    }

    public void add(Comment tempComment) {
        if(comments == null) {
            comments = new ArrayList<>();
        }

        comments.add(tempComment);
        tempComment.setUser(this);
    }
}
