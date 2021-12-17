package response.editorResponse;

import response.appResponse.AppResponse;
import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class CompileCodeDocResponse extends AppResponse implements Serializable {

    private String error;
    private Status status;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.COMPILE_CODEDOC_RESPONSE;
    }
}