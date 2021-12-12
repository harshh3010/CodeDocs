package response.appResponse;

import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class AddAccessorToCodeDocResponse extends AppResponse implements Serializable {

    private Status status;

    public AddAccessorToCodeDocResponse() {
    }

    public AddAccessorToCodeDocResponse(Status status) {
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
        return ResponseType.ADD_ACCESSOR_TO_CODEDOC_RESPONSE;
    }
}
