package comments.model;

import javax.persistence.*;

@Entity
@Table(name="Comment")
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;
    public String content;

    public Comment() {
    }

    public Comment(String content) {
        this.content = content;
    }
}
