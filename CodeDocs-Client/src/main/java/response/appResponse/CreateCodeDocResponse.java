package response.appResponse;


import utilities.ResponseType;
import utilities.Status;

import java.io.Serializable;
import java.util.Date;

public class CreateCodeDocResponse extends AppResponse implements Serializable {

    private Date createdAt;
    private Date updatedAt;
    private String codeDocId;
    private Status status;

    public CreateCodeDocResponse() {
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCodeDocId() {
        return codeDocId;
    }

    public void setCodeDocId(String codeDocId) {
        this.codeDocId = codeDocId;
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