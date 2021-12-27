package requests.peerRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to send updated contents to user asking for sync
 */
public class UpdateContentRequest extends AppRequest implements Serializable {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE_CONTENT_REQUEST;
    }
}
