package response.editorResponse;

import response.appResponse.AppResponse;
import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class RunCodeDocResponse extends AppResponse implements Serializable {

    private String output;
    private String error;
    private Status status;

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

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
        return ResponseType.RUN_CODEDOC_RESPONSE;
    }
}
