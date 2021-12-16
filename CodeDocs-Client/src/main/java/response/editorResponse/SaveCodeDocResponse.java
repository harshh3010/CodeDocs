package response.editorResponse;

import response.appResponse.AppResponse;
import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class SaveCodeDocResponse extends AppResponse implements Serializable {

    private Status status;

    public SaveCodeDocResponse() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.SAVE_CODEDOC_RESPONSE;
    }
}
