package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to update a codedoc
 */
public class UpdateCodeDocRequest extends AppRequest implements Serializable {

    private String codeDocID;
    private String userID;
    private String title;
    private String description;

    public UpdateCodeDocRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

        return RequestType.UPDATE_CODEDOC_REQUEST;
    }
}
