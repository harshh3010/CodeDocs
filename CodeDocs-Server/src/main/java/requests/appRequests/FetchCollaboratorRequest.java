package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

public class FetchCollaboratorRequest extends AppRequest implements Serializable {

    private String ownerID;
    private int offset;
    private int rowcount;
    private String codeDocID;

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getRowcount() {
        return rowcount;
    }

    public void setRowcount(int rowcount) {
        this.rowcount = rowcount;
    }

    public String getCodeDocID() {
        return codeDocID;
    }

    public void setCodeDocID(String codeDocID) {
        this.codeDocID = codeDocID;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.FETCH_COLLABORATOR_REQUEST;
    }
}