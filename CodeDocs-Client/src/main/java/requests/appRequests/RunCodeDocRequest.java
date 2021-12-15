package requests.appRequests;

import utilities.LanguageType;
import utilities.RequestType;
import java.io.Serializable;

public class RunCodeDocRequest extends AppRequest implements Serializable {


    private String userID;
    private String codeDocID;
    private LanguageType languageType;
    //TODO: content usse maangna h ky yaaa khudse read krne k h ??


    public RunCodeDocRequest() {
    }

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
        return RequestType.RUN_CODEDOC_REQUEST;
    }
}
