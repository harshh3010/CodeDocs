package requests.editorRequests;

import models.CodeDoc;
import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to save the contents of a codedoc
 */
public class SaveCodeDocRequest extends AppRequest implements Serializable {

    private String userId;
    private CodeDoc codeDoc;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CodeDoc getCodeDoc() {
        return codeDoc;
    }

    public void setCodeDoc(CodeDoc codeDoc) {
        this.codeDoc = codeDoc;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SAVE_CODEDOC_REQUEST;
    }
}
