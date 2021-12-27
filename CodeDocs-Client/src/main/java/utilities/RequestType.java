package utilities;

/**
 * Different request types to send to server
 */
public enum RequestType {
    SIGNUP_REQUEST,
    LOGIN_REQUEST,
    LOGOUT_REQUEST,
    CREATE_CODEDOC_REQUEST,
    GET_ME_REQUEST,
    VERIFY_USER_REQUEST,
    FETCH_CODEDOC_REQUEST,
    ADD_ACCESSOR_TO_CODEDOC_REQUEST,
    UPDATE_CODEDOC_REQUEST,
    DELETE_CODEDOC_REQUEST,
    LOAD_EDITOR_REQUEST,
    INVITE_COLLABORATOR_REQUEST,
    REMOVE_COLLABORATOR_REQUEST,
    CHANGE_COLLABORATOR_RIGHTS_REQUEST,
    FETCH_COLLABORATOR_REQUEST,
    ACCEPT_INVITE_REQUEST,
    REJECT_INVITE_REQUEST,
    FETCH_INVITE_REQUEST,
    RUN_CODEDOC_REQUEST,
    COMPILE_CODEDOC_REQUEST,
    SAVE_CODEDOC_REQUEST,
    EDITOR_CONNECTION_REQUEST,
    SEND_PEER_CONNECTION_REQUEST,
    SEND_PEER_INFO_REQUEST,
    EDITOR_CLOSE_REQUEST,
    STREAM_CONTENT_CHANGES_REQUEST,
    STREAM_CONTENT_SELECTION_REQUEST,
    STREAM_CURSOR_POSITION_REQUEST,
    TRANSFER_CONTROL_REQUEST,
    SEND_MESSAGE_REQUEST,
    TAKE_CONTROL_REQUEST,
    CONTROL_SWITCH_REQUEST,
    SYNC_CONTENT_REQUEST,
    UPDATE_CONTENT_REQUEST,
}
