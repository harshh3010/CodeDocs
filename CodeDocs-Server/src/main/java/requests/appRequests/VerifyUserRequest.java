package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

public class VerifyUserRequest extends AppRequest implements Serializable {

    private String userID;
    private String verificationToken;

    public VerifyUserRequest(String userID, String verificationToken) {
        this.userID = userID;
        this.verificationToken = verificationToken;
    }

    public VerifyUserRequest() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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
