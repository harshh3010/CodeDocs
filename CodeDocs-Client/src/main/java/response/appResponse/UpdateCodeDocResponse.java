package response.appResponse;

import utilities.ResponseType;
import utilities.SignupStatus;
import utilities.Status;

import java.io.Serializable;

public class UpdateCodeDocResponse extends AppResponse implements Serializable {

    private Status status;
    public UpdateCodeDocResponse() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public ResponseType getResponseType() {

        return ResponseType.UPDATE_CODEDOC_RESPONSE;
    }
}
