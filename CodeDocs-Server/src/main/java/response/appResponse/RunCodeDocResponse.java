package response.appResponse;

import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;

public class RunCodeDocResponse extends AppResponse implements Serializable {

    private String output;
    private String error;
    private int exitStatus;
    private Status status;

    public RunCodeDocResponse() {
    }

    public int getExitStatus() {
        return exitStatus;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setExitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
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
