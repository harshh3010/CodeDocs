package requests.editorRequests;

import requests.appRequests.AppRequest;
import utilities.LanguageType;
import utilities.RequestType;

import java.io.Serializable;

public class RunCodeDocRequest extends AppRequest implements Serializable {


    private String userID;
    private String codeDocID;
    private LanguageType languageType;
    private String input;

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

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.RUN_CODEDOC_REQUEST;
    }
}
