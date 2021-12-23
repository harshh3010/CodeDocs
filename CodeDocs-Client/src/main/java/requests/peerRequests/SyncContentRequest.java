package requests.peerRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

public class SyncContentRequest extends AppRequest implements Serializable {
    @Override
    public RequestType getRequestType() {
        return RequestType.SYNC_CONTENT_REQUEST;
    }
}
