package requests.peerRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

public class StreamCursorPositionRequest extends AppRequest implements Serializable {

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.STREAM_CURSOR_POSITION_REQUEST;
    }
}
