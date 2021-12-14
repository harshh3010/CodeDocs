package response.appResponse;

import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class InviteCollaboratorResponse extends AppResponse implements Serializable {

    private Status status;

    public InviteCollaboratorResponse() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public ResponseType getResponseType() {
        return ResponseType.INVITE_COLLABORATOR_RESPONSE;
    }
}
