package users.model;

import users.model.User;

public class UserResource {
    private String id;
    private String link;
    private String firstName;
    private String lastName;

    public UserResource() {
    }

    public UserResource(String id, String link, String firstName, String lastName) {
        this.id = id;
        this.link = link;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserResource(User user, String link) {
        this.id = user.id.toString();
        this.link = link;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
