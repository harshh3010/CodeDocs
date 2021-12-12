package response.appResponse;
import utilities.EditorResponseType;

import java.io.Serializable;

public abstract class EditorResponse implements Serializable {

    public abstract EditorResponseType getResponseType();
}
