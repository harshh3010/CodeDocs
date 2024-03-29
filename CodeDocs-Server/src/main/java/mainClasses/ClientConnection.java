package mainClasses;

import requests.appRequests.*;
import requests.editorRequests.*;
import response.appResponse.GetMeResponse;
import response.appResponse.LoginResponse;
import services.DestroyResources;
import services.clientServices.*;
import utilities.RequestType;
import utilities.Status;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class is used for starting a new thread when a new client connects with the server
 */
public class ClientConnection extends Thread {

    private final Socket client;
    private final ObjectInputStream inputStream; // For reading client requests
    private final ObjectOutputStream outputStream; // For writing responses to client

    public String clientUserID = ""; // User id of connected client

    public ClientConnection(Socket client) throws IOException {

        // Ip address of connected client
        String ipAddress = client.getInetAddress().getCanonicalHostName();
        System.out.println("Client connected " + ipAddress);

        this.client = client;
        this.outputStream = new ObjectOutputStream(client.getOutputStream());
        this.inputStream = new ObjectInputStream(client.getInputStream());
    }

    @Override
    public void run() {

        // Handle the requests from the client
        while (true) {
            try {

                // Reading the request made by client
                AppRequest request = (AppRequest) inputStream.readObject();

                // Performing different actions depending on the type of request received
                if (request.getRequestType() == RequestType.SIGNUP_REQUEST) {
                    System.out.println("Client wants to signup!");
                    outputStream.writeObject(AuthenticationService.registerUser((SignupRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.LOGIN_REQUEST) {
                    System.out.println("Client wants to login!");
                    LoginResponse response = AuthenticationService.loginUser((LoginRequest) request);
                    clientUserID = response.getUser().getUserID();
                    outputStream.writeObject(response);
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.VERIFY_USER_REQUEST) {
                    System.out.println("Client wants to verify his account!");
                    outputStream.writeObject(AuthenticationService.verifyUser((VerifyUserRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.GET_ME_REQUEST) {
                    System.out.println("Client wants to fetch his info!");
                    GetMeResponse response = UserService.getUserData((GetMeRequest) request);
                    if (response.getStatus() == Status.SUCCESS) {
                        clientUserID = response.getUser().getUserID();
                    }
                    outputStream.writeObject(response);
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.CREATE_CODEDOC_REQUEST) {
                    System.out.println("Client wants to create a CodeDoc!");
                    outputStream.writeObject(CodeDocService.createCodeDoc((CreateCodeDocRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.FETCH_CODEDOC_REQUEST) {
                    System.out.println("Client wants to fetch CodeDoc!");
                    outputStream.writeObject(CodeDocService.fetchCodeDoc((FetchCodeDocRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.DELETE_CODEDOC_REQUEST) {
                    System.out.println("Client wants to delete CodeDoc!");
                    outputStream.writeObject(CodeDocService.deleteCodeDoc((DeleteCodeDocRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.UPDATE_CODEDOC_REQUEST) {
                    System.out.println("Client wants to update CodeDoc details!");
                    outputStream.writeObject(CodeDocService.updateCodeDoc((UpdateCodeDocRequest) request));
                } else if (request.getRequestType() == RequestType.LOAD_EDITOR_REQUEST) {
                    System.out.println("Client wants to open editor!");
                    outputStream.writeObject(EditorService.loadEditor((LoadEditorRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.SAVE_CODEDOC_REQUEST) {
                    System.out.println("Client wants to save codeDoc!");
                    outputStream.writeObject(EditorService.saveCodeDoc((SaveCodeDocRequest) request));
                    outputStream.flush();
                    outputStream.reset();
                } else if (request.getRequestType() == RequestType.INVITE_COLLABORATOR_REQUEST) {
                    System.out.println("Client wants to invite a collaborator!");
                    outputStream.writeObject(CollaborationService.inviteCollaborator((InviteCollaboratorRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.REMOVE_COLLABORATOR_REQUEST) {
                    System.out.println("Client wants to remove a collaborator!");
                    outputStream.writeObject(CollaborationService.removeCollaborator((RemoveCollaboratorRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.FETCH_COLLABORATOR_REQUEST) {
                    System.out.println("Client wants to fetch collaborator!");
                    outputStream.writeObject(CollaborationService.fetchCollaborators((FetchCollaboratorRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.CHANGE_COLLABORATOR_RIGHTS_REQUEST) {
                    System.out.println("Client wants to change rights of a collaborator!");
                    outputStream.writeObject(CollaborationService.changeCollaboratorRights((ChangeCollaboratorRightsRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.ACCEPT_INVITE_REQUEST) {
                    System.out.println("Client wants to accept invite !");
                    outputStream.writeObject(CollaborationService.acceptInvite((AcceptInviteRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.REJECT_INVITE_REQUEST) {
                    System.out.println("Client wants to reject invite !");
                    outputStream.writeObject(CollaborationService.rejectInvite((RejectInviteRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.FETCH_INVITE_REQUEST) {
                    System.out.println("Client wants to fetch his/her invite !");
                    outputStream.writeObject(CollaborationService.fetchInvites((FetchInviteRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.RUN_CODEDOC_REQUEST) {
                    System.out.println("Client wants to run codeDoc !");
                    outputStream.writeObject(CompileService.runCodeDoc((RunCodeDocRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.COMPILE_CODEDOC_REQUEST) {
                    System.out.println("Client wants to compile codeDoc !");
                    outputStream.writeObject(CompileService.compileCodeDoc((CompileCodeDocRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.EDITOR_CONNECTION_REQUEST) {
                    System.out.println("Client wants to start codedoc editor!");
                    outputStream.writeObject(EditorService.establishConnection((EditorConnectionRequest) request, client.getInetAddress().getHostAddress()));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.EDITOR_CLOSE_REQUEST) {
                    System.out.println("Client wants to close codedoc editor!");
                    EditorService.destroyConnection((EditorCloseRequest) request);
                } else if (request.getRequestType() == RequestType.TRANSFER_CONTROL_REQUEST) {
                    System.out.println("Client wants to transfer code editor control!");
                    EditorService.transferControl((TransferControlRequest) request);
                }

            } catch (IOException | ClassNotFoundException e) {
                if (!clientUserID.isEmpty()) {
                    // Mark the client as offline
                    DestroyResources.destroyAllocations(clientUserID);
                }
                System.out.println("Client " + clientUserID + " disconnected!");
                break;
            }
        }
    }


}
