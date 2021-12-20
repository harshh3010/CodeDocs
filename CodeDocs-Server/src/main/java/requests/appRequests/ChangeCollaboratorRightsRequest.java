package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

public class ChangeCollaboratorRightsRequest extends AppRequest implements Serializable {

    private String ownerID;
    private String collaboratorID;
    private String codeDocID;
    private int writePermissions;


    public String getOwnerID() {
        return ownerID;
    }

    public int getWritePermissions() {
        return writePermissions;
    }

    public void setWritePermissions(int writePermissions) {
        this.writePermissions = writePermissions;
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
        return RequestType.CHANGE_COLLABORATOR_RIGHTS_REQUEST;
    }
}
