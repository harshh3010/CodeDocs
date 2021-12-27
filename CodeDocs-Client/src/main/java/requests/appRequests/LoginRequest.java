package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to log-in the application
 */
public class LoginRequest extends AppRequest implements Serializable {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.LOGIN_REQUEST;
    }
}
