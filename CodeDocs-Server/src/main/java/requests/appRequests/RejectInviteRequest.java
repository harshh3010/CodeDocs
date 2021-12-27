package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to reject an invitation
 */
public class RejectInviteRequest extends AppRequest implements Serializable {


    private String codeDocID;
    private String receiverID;

    public RejectInviteRequest() {
    }

    public String getCodeDocID() {
        return codeDocID;
    }

    public void setCodeDocID(String codeDocID) {
        this.codeDocID = codeDocID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.REJECT_INVITE_REQUEST;
    }
}