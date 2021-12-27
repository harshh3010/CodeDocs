package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to invite a user to collaborate to our codedoc
 */
public class InviteCollaboratorRequest extends AppRequest implements Serializable {

    private String senderID;
    private String receiverEmail;
    private String codeDocID;
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

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
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