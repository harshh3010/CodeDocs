package requests.editorRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

public class EditorConnectionRequest extends AppRequest implements Serializable {

    private int port;
    private String userId;
    private String codeDocId;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCodeDocId() {
        return codeDocId;
    }

    public void setCodeDocId(String codeDocId) {
        this.codeDocId = codeDocId;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.EDITOR_CONNECTION_REQUEST;
    }
}
