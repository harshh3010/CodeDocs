package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to verify our account
 */
public class VerifyUserRequest extends AppRequest implements Serializable {

    private String userEmail;
    private String verificationToken;

    public VerifyUserRequest(String userEmail, String verificationToken) {
        this.userEmail = userEmail;
        this.verificationToken = verificationToken;
    }

    public VerifyUserRequest() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.VERIFY_USER_REQUEST;
    }
}
