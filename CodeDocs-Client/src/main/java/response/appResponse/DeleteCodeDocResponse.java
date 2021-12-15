package response.appResponse;

import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class DeleteCodeDocResponse extends AppResponse implements Serializable {

    private Status status;
    public DeleteCodeDocResponse() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public ResponseType getResponseType() {

        return ResponseType.DELETE_CODEDOC_RESPONSE;
    }
}
