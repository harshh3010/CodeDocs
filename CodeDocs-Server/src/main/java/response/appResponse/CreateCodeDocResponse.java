package response.appResponse;


import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class CreateCodeDocResponse extends AppResponse implements Serializable {

    private Status status;

    public CreateCodeDocResponse() {
    }

    public CreateCodeDocResponse(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.CREATE_CODEDOC_RESPONSE;
    }
}