package models;

import java.io.Serializable;

/**
 * Collaborator represents users having access to specific codedoc
 */
public class Collaborator implements Serializable {

    private User user;
    private int writePermissions;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getWritePermissions() {
        return writePermissions;
    }

    public void setWritePermissions(int writePermissions) {
        this.writePermissions = writePermissions;
    }
}
