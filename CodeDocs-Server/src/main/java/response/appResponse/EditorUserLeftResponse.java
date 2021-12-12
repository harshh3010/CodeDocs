package response.appResponse;

import models.User;
import utilities.EditorResponseType;

import java.io.Serializable;

public class EditorUserLeftResponse extends EditorResponse implements Serializable {

    private User user;

    public EditorUserLeftResponse() {
    }

    public EditorUserLeftResponse(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public EditorResponseType getResponseType() {
        return EditorResponseType.USER_LEFT_RESPONSE;
    }
}
