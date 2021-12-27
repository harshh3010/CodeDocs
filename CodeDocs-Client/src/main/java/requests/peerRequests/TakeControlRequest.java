package requests.peerRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to take control of code editor
 */
public class TakeControlRequest extends AppRequest implements Serializable {
    @Override
    public RequestType getRequestType() {
        return RequestType.TAKE_CONTROL_REQUEST;
    }
}
