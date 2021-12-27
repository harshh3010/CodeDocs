package requests.peerRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to send cursor position to other peers
 */
public class StreamCursorPositionRequest extends AppRequest implements Serializable {

    private String userId;
    private int position;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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
