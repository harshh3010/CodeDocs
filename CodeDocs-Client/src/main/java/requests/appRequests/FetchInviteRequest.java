package requests.appRequests;

import utilities.CodeDocRequestType;
import utilities.RequestType;

import java.io.Serializable;

public class FetchInviteRequest extends AppRequest implements Serializable {

    private String userID;
    private int offset;
    private int rowcount;

    public FetchInviteRequest() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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
        return RequestType.FETCH_INVITE_REQUEST;
    }
}
