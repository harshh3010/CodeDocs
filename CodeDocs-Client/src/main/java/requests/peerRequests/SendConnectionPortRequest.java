package requests.peerRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

public class SendConnectionPortRequest extends AppRequest implements Serializable {

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SEND_CONNECTION_PORT_REQUEST;
    }
}
