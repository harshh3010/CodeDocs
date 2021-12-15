package response.appResponse;

import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class RejectInviteResponse extends AppResponse implements Serializable {

    private Status status;

    public RejectInviteResponse() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public ResponseType getResponseType() {
        return ResponseType.REJECT_INVITE_RESPONSE;
    }
}
