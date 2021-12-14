package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

public class InviteCollaboratorRequest extends AppRequest implements Serializable {

    private String senderID;
    private String receiverID;
    private String codeDocID;
    // 0 - no write permissions ;  1 - has write permissions
    private int writePermissions;


    public InviteCollaboratorRequest() {
    }

    public String getSenderID() {
        return senderID;
    }

    public int getWritePermissions() {
        return writePermissions;
    }

    public void setWritePermissions(int writePermissions) {
        this.writePermissions = writePermissions;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getCodeDocID() {
        return codeDocID;
    }

    public void setCodeDocID(String codeDocID) {
        this.codeDocID = codeDocID;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.INVITE_COLLABORATOR_REQUEST;
    }
}