package requests.appRequests;

import utilities.CodeDocRequestType;
import utilities.RequestType;

import java.io.Serializable;

public class FetchCodeDocRequest extends AppRequest implements Serializable {

    private String codeDocID;
    private String userID;
    private CodeDocRequestType codeDocRequestType;
    private int offset;
    private int rowcount;

    public FetchCodeDocRequest() {
    }

    /**
     * when user wants to fetch a particular codeDoc (using codeDocID)
     * @param codeDocID
     */
    public FetchCodeDocRequest(String codeDocID) {
        this.codeDocID = codeDocID;
    }

    /**
     * when user wants to fetch his personal codeDocs
     * offset will neglect that amount of top rows
     * and return after that rows equal to rowCount
     * @param userID
     * @param codeDocRequestType
     * @param offset
     * @param rowcount
     */
    public FetchCodeDocRequest(String userID, CodeDocRequestType codeDocRequestType, int offset, int rowcount) {
        this.userID = userID;
        this.codeDocRequestType = codeDocRequestType;
        this.offset = offset;
        this.rowcount = rowcount;
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
