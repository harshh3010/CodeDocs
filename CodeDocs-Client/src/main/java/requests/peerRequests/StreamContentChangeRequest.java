package requests.peerRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to stream content changes to other peers
 */
public class StreamContentChangeRequest extends AppRequest implements Serializable {

    private int insertedStart;
    private String insertedContent;
    private int removedEnd;
    private int removedLength;


    public int getRemovedEnd() {
        return removedEnd;
    }

    public void setRemovedEnd(int removedEnd) {
        this.removedEnd = removedEnd;
    }

    public int getRemovedLength() {
        return removedLength;
    }

    public void setRemovedLength(int removedLength) {
        this.removedLength = removedLength;
    }

    public int getInsertedStart() {
        return insertedStart;
    }

    public void setInsertedStart(int insertedStart) {
        this.insertedStart = insertedStart;
    }

    public String getInsertedContent() {
        return insertedContent;
    }

    public void setInsertedContent(String insertedContent) {
        this.insertedContent = insertedContent;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.STREAM_CONTENT_CHANGES_REQUEST;
    }
}
