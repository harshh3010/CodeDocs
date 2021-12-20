package response.appResponse;

import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class RemoveCollaboratorResponse extends AppResponse implements Serializable {

    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public ResponseType getResponseType() {
        return ResponseType.REMOVE_COLLABORATOR_RESPONSE;
    }
}