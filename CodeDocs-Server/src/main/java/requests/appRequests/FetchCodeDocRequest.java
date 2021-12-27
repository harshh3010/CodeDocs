package requests.appRequests;

import utilities.CodeDocRequestType;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to fetch codedocs from the server
 */
public class FetchCodeDocRequest extends AppRequest implements Serializable {

    private String codeDocID;
    private String userID;
    private CodeDocRequestType codeDocRequestType;
    private int offset;
    private int rowcount;

    public FetchCodeDocRequest() {
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

    public CodeDocRequestType getCodeDocRequestType() {
        return codeDocRequestType;
    }

    public void setCodeDocRequestType(CodeDocRequestType codeDocRequestType) {
        this.codeDocRequestType = codeDocRequestType;
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

    @Override
    public RequestType getRequestType() {
        return RequestType.FETCH_CODEDOC_REQUEST;
    }
}
