package response.appResponse;
import utilities.EditorResponseType;

import java.io.Serializable;

public class EditorContentUpdateResponse extends EditorResponse implements Serializable {

    private String content;

    public EditorContentUpdateResponse() {
    }

    public EditorContentUpdateResponse(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public EditorResponseType getResponseType() {
        return EditorResponseType.CONTENT_UPDATE_RESPONSE;
    }
}
