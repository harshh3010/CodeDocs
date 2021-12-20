package response.appResponse;

import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class ChangeCollaboratorRightsResponse extends AppResponse implements Serializable {

    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public ResponseType getResponseType() {
        return ResponseType.CHANGE_COLLABORATOR_RIGHTS_RESPONSE;
    }
}