package requests.editorRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

public class TransferControlRequest extends AppRequest implements Serializable {

    private String codeDocId;
    private String userId;

    public String getCodeDocId() {
        return codeDocId;
    }

    public void setCodeDocId(String codeDocId) {
        this.codeDocId = codeDocId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.TRANSFER_CONTROL_REQUEST;
    }
}
