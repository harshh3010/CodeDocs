package response.appResponse;

import models.CodeDoc;
import utilities.CodeDocRequestType;
import utilities.ResponseType;
import utilities.Status;
import java.io.Serializable;
import java.util.List;

public class FetchCodeDocResponse extends AppResponse implements Serializable {

    private List<CodeDoc> codeDocs;
    private Status status;//to know if fetch was successful or not
    private CodeDocRequestType codeDocRequestType; // to know that it is a response corr to which type of fetch request

    public FetchCodeDocResponse() {
    }

    public FetchCodeDocResponse(List<CodeDoc> codeDocs, Status status, CodeDocRequestType codeDocRequestType) {
        this.codeDocs = codeDocs;
        this.status = status;
        this.codeDocRequestType = codeDocRequestType;
    }

    public List<CodeDoc> getCodeDocs() {
        return codeDocs;
    }

    public void setCodeDocs(List<CodeDoc> codeDocs) {
        this.codeDocs = codeDocs;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public CodeDocRequestType getCodeDocRequestType() {
        return codeDocRequestType;
    }

    public void setCodeDocRequestType(CodeDocRequestType codeDocRequestType) {
        this.codeDocRequestType = codeDocRequestType;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.FETCH_CODEDOC_RESPONSE;
    }
}
