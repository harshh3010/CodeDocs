package requests.peerRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

public class StreamContentChangeRequest extends AppRequest implements Serializable {

    private int startPosition;
    private String content;

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.STREAM_CONTENT_CHANGES_REQUEST;
    }
}
