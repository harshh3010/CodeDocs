package response.appResponse;
import models.Collaborator;
import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;
import java.util.*;

public class FetchCollaboratorResponse extends AppResponse implements Serializable {

    private List<Collaborator> collaborators;
    private Status status;//to know if fetch was successul or not

    public List<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.FETCH_COLLABORATOR_RESPONSE;
    }
}
