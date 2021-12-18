package requests.peerRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

public class StreamContentSelectionRequest extends AppRequest implements Serializable {

    private int start;
    private int end;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.STREAM_CONTENT_SELECTION_REQUEST;
    }
}
