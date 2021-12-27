package requests.editorRequests;

import requests.appRequests.AppRequest;
import utilities.LanguageType;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to compile a codedoc
 */
public class CompileCodeDocRequest extends AppRequest implements Serializable {

    private String userID;
    private String codeDocID;
    private LanguageType languageType;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCodeDocID() {
        return codeDocID;
    }

    public void setCodeDocID(String codeDocID) {
        this.codeDocID = codeDocID;
    }

    public LanguageType getLanguageType() {
        return languageType;
    }

    public void setLanguageType(LanguageType languageType) {
        this.languageType = languageType;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.COMPILE_CODEDOC_REQUEST;
    }
}