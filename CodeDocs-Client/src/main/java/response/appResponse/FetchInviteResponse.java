package response.appResponse;

import models.CodeDoc;
import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;
import java.util.List;

public class FetchInviteResponse extends AppResponse implements Serializable {

    private List<CodeDoc> invites;
    private Status status;//to know if fetch was successul or not

    public FetchInviteResponse() {
    }

    public List<CodeDoc> getInvites() {
        return invites;
    }

    public void setInvites(List<CodeDoc> invites) {
        this.invites = invites;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.FETCH_INVITE_RESPONSE;
    }
}
