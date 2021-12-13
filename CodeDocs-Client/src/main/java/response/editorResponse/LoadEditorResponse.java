package response.editorResponse;

import response.appResponse.AppResponse;
import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class LoadEditorResponse extends AppResponse implements Serializable {

    private Status status;
    private String content;
    private boolean hasWritePermissions;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHasWritePermissions() {
        return hasWritePermissions;
    }

    public void setHasWritePermissions(boolean hasWritePermissions) {
        this.hasWritePermissions = hasWritePermissions;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.LOAD_EDITOR_RESPONSE;
    }
}
