package requests.editorRequests;

import requests.appRequests.AppRequest;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to establish editor connection
 */
public class EditorConnectionRequest extends AppRequest implements Serializable {

    private int port;
    private int audioPort;
    private String userId;
    private String codeDocId;

    public int getAudioPort() {
        return audioPort;
    }

    public void setAudioPort(int audioPort) {
        this.audioPort = audioPort;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCodeDocId() {
        return codeDocId;
    }

    public void setCodeDocId(String codeDocId) {
        this.codeDocId = codeDocId;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.EDITOR_CONNECTION_REQUEST;
    }
}
