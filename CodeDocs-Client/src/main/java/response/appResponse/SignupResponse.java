package response.appResponse;

import utilities.ResponseType;
import utilities.SignupStatus;

import java.io.Serializable;

public class SignupResponse extends AppResponse implements Serializable {

    private SignupStatus signupStatus;

    public SignupResponse() {
    }

    public SignupResponse(SignupStatus signupStatus) {
        this.signupStatus = signupStatus;
    }

    public SignupStatus getSignupStatus() {
        return signupStatus;
    }

    public void setSignupStatus(SignupStatus signupStatus) {
        this.signupStatus = signupStatus;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.SIGNUP_RESPONSE;
    }
}
