package requests.peerRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to inform peers about control switch in editor
 */
public class ControlSwitchRequest extends AppRequest implements Serializable {

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.CONTROL_SWITCH_REQUEST;
    }
}
