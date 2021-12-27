package requests.appRequests;

import models.CodeDoc;
import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to create a new codedoc
 */
public class CreateCodeDocRequest extends AppRequest implements Serializable {

    private CodeDoc codeDoc;

    public CreateCodeDocRequest() {
    }

    public CreateCodeDocRequest(CodeDoc codeDoc) {
        this.codeDoc = codeDoc;
    }

    public CodeDoc getCodeDoc() {
        return codeDoc;
    }

    public void setCodeDoc(CodeDoc codeDoc) {
        this.codeDoc = codeDoc;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.CREATE_CODEDOC_REQUEST;
    }

}
