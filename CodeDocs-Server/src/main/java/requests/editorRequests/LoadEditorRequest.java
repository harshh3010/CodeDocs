package requests.editorRequests;

import requests.appRequests.AppRequest;
import utilities.LanguageType;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to load editor contents from the server
 */
public class LoadEditorRequest extends AppRequest implements Serializable {

    private String userId;
    private String codeDocId;
    private LanguageType languageType;

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

    public LanguageType getLanguageType() {
        return languageType;
    }

    public void setLanguageType(LanguageType languageType) {
        this.languageType = languageType;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.LOAD_EDITOR_REQUEST;
    }
}
