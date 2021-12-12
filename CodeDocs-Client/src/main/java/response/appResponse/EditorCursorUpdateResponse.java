package response.appResponse;

import utilities.EditorResponseType;

import java.io.Serializable;

public class EditorCursorUpdateResponse extends EditorResponse implements Serializable {

    private int cursorPosition;

    public EditorCursorUpdateResponse() {
    }

    public EditorCursorUpdateResponse(int cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(int cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    @Override
    public EditorResponseType getResponseType() {
        return EditorResponseType.CURSOR_UPDATE_RESPONSE;
    }
}
