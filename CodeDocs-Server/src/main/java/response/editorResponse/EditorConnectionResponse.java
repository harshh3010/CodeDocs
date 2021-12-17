package response.editorResponse;

import models.Peer;
import response.appResponse.AppResponse;
import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;
import java.util.ArrayList;

public class EditorConnectionResponse extends AppResponse implements Serializable {

    private Status status;
    private boolean hasWritePermissions;
    private boolean hasControl;
    private ArrayList<Peer> activePeers;

    public boolean isHasWritePermissions() {
        return hasWritePermissions;
    }

    public void setHasWritePermissions(boolean hasWritePermissions) {
        this.hasWritePermissions = hasWritePermissions;
    }

    public boolean isHasControl() {
        return hasControl;
    }

    public void setHasControl(boolean hasControl) {
        this.hasControl = hasControl;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ArrayList<Peer> getActivePeers() {
        return activePeers;
    }

    public void setActivePeers(ArrayList<Peer> activePeers) {
        this.activePeers = activePeers;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.EDITOR_CONNECTION_RESPONSE;
    }
}
