package requests.editorRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to destroy editor connection
 */
public class EditorCloseRequest extends AppRequest implements Serializable {

    private String userId;
    private String codeDocId;
    private String userInControl;

    public String getUserInControl() {
        return userInControl;
    }

    public void setUserInControl(String userInControl) {
        this.userInControl = userInControl;
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
        return RequestType.EDITOR_CLOSE_REQUEST;
    }
}
