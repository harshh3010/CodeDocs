package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to remove a collaborator from a codedoc
 */
public class RemoveCollaboratorRequest extends AppRequest implements Serializable {

    private String ownerID;
    private String collaboratorID;
    private String codeDocID;

    public RemoveCollaboratorRequest() {
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getCollaboratorID() {
        return collaboratorID;
    }

    public void setCollaboratorID(String collaboratorID) {
        this.collaboratorID = collaboratorID;
    }

    public String getCodeDocID() {
        return codeDocID;
    }

    public void setCodeDocID(String codeDocID) {
        this.codeDocID = codeDocID;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.REMOVE_COLLABORATOR_REQUEST;
    }
}