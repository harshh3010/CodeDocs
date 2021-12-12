package requests.appRequests;

import models.User;
import utilities.RequestType;

import java.io.Serializable;

public class SignupRequest extends AppRequest implements Serializable {

    private User user;

    public SignupRequest(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SIGNUP_REQUEST;
    }
}
