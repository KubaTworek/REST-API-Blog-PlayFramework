package users.model;

public class UserResource {
    private String id;
    private String firstName;
    private String lastName;
    private String link;

    public UserResource() {
    }

    public UserResource(String id, String firstName, String lastName, String link) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.link = link;
    }

    public UserResource(User user, String link) {
        this.id = user.id.toString();
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLink() {
        return link;
    }
}