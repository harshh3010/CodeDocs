package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to delete an existing codedoc
 */
public class DeleteCodeDocRequest extends AppRequest implements Serializable {

    private String codeDocID;
    private String userID;

    public DeleteCodeDocRequest() {
    }

    public String getCodeDocID() {
        return codeDocID;
    }

    public void setCodeDocID(String codeDocID) {
        this.codeDocID = codeDocID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.DELETE_CODEDOC_REQUEST;
    }
}