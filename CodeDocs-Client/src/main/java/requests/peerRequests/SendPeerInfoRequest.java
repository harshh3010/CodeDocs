package requests.peerRequests;

import models.User;
import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

public class SendPeerInfoRequest extends AppRequest implements Serializable {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SEND_PEER_INFO_REQUEST;
    }
}
