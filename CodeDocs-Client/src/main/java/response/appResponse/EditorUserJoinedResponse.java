package response.appResponse;

import models.User;
import utilities.EditorResponseType;

import java.io.Serializable;

public class EditorUserJoinedResponse extends EditorResponse implements Serializable {

    private User user;

    public EditorUserJoinedResponse() {
    }

    public EditorUserJoinedResponse(User user) {
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
        return EditorResponseType.USER_JOINED_RESPONSE;
    }
}
