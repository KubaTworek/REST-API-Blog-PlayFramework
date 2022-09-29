package posts.model;

import javax.persistence.*;

@Entity
@Table(name="Post")
public class Post {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;
    public String title;
    public String text;

    public Post() {
    }

    public Post(String title, String text) {
        this.title = title;
        this.text = text;
    }
}
