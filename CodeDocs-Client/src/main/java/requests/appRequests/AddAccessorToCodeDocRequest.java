package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to add a specified user to access the codedoc
 */
public class AddAccessorToCodeDocRequest extends AppRequest implements Serializable {

    private String ownerID;
    private String accessorID;
    private String codeDocID;

    public AddAccessorToCodeDocRequest() {
    }

    public AddAccessorToCodeDocRequest(String ownerID, String accessorID, String codeDocID) {
        this.ownerID = ownerID;
        this.accessorID = accessorID;
        this.codeDocID = codeDocID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getAccessorID() {
        return accessorID;
    }

    public void setAccessorID(String accessorID) {
        this.accessorID = accessorID;
    }

    public String getCodeDocID() {
        return codeDocID;
    }

    public void setCodeDocID(String codeDocID) {
        this.codeDocID = codeDocID;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.ADD_ACCESSOR_TO_CODEDOC_REQUEST;
    }

}
