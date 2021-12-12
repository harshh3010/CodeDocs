package response.appResponse;

import models.User;
import utilities.LoginStatus;
import utilities.ResponseType;

import java.io.Serializable;

public class LoginResponse extends AppResponse implements Serializable {
    private String token;
    private User user;
    private LoginStatus loginStatus;

    public LoginResponse() {
    }

    public LoginResponse(String token, User user, LoginStatus loginStatus) {
        this.token = token;
        this.user = user;
        this.loginStatus = loginStatus;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    @Override
    public ResponseType getResponseType() {
        return ResponseType.LOGIN_RESPONSE;
    }
}

