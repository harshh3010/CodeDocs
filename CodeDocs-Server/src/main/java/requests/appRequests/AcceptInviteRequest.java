package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

public class AcceptInviteRequest extends AppRequest implements Serializable {


    private String codeDocID;
    private String receiverID;

    public AcceptInviteRequest() {
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
        return RequestType.ACCEPT_INVITE_REQUEST;
    }
}
