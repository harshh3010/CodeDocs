package response.appResponse;

import models.User;
import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class GetMeResponse extends AppResponse implements Serializable {

    private Status status;
    private User user;

    public GetMeResponse() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.GET_ME_RESPONSE;
    }
}
