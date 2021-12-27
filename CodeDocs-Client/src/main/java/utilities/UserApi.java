package utilities;

import models.User;

/**
 * UserApi class to allow global access to basic user details
 */
public class UserApi {

    private String id;
    private String token;
    private String email;
    private String firstName;
    private String lastName;

    private static UserApi instance = null;

    // private constructor restricted to this class itself
    private UserApi() {
    }

    // static method to create instance of Singleton class
    public static UserApi getInstance() {
        if (instance == null)
            instance = new UserApi();
        return instance;
    }

    public void setUserData(User user) {
        this.id = user.getUserID();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public User getUser() {
        User user = new User();
        user.setUserID(this.id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        return user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public static void setInstance(UserApi instance) {
        UserApi.instance = instance;
    }
}