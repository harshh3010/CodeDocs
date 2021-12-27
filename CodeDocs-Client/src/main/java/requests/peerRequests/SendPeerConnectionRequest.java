package requests.peerRequests;

import models.User;
import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to send connection info to other peers
 */
public class SendPeerConnectionRequest extends AppRequest implements Serializable {

    private int port;
    private int audioPort;
    private boolean hasWritePermissions;
    private User user;

    public int getAudioPort() {
        return audioPort;
    }

    public void setAudioPort(int audioPort) {
        this.audioPort = audioPort;
    }

    public boolean isHasWritePermissions() {
        return hasWritePermissions;
    }

    public void setHasWritePermissions(boolean hasWritePermissions) {
        this.hasWritePermissions = hasWritePermissions;
    }

    public int getPort() {
        return port;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SEND_PEER_CONNECTION_REQUEST;
    }
}
