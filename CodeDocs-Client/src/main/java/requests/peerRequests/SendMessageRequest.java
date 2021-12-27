package requests.peerRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to send message to other peers
 */
public class SendMessageRequest extends AppRequest implements Serializable {

    private boolean isPrivate;
    private String content;

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SEND_MESSAGE_REQUEST;
    }
}
