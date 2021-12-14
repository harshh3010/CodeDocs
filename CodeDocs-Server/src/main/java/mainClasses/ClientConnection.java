package mainClasses;

import requests.appRequests.*;
import requests.editorRequests.LoadEditorRequest;
import response.appResponse.FetchInviteResponse;
import services.clientServices.*;
import utilities.RequestType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection extends Thread {

    private final String ipAddress;
    private final Socket client;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public ClientConnection(Socket client) throws IOException {

        ipAddress = client.getInetAddress().getCanonicalHostName();
        System.out.println("Client connected " + ipAddress);

        this.client = client;
        this.outputStream = new ObjectOutputStream(client.getOutputStream());
        this.inputStream = new ObjectInputStream(client.getInputStream());
    }

    @Override
    public void run() {

        while (true) {
            try {
                AppRequest request = (AppRequest) inputStream.readObject();

                if (request.getRequestType() == RequestType.SIGNUP_REQUEST) {
                    System.out.println("Client wants to signup!");
                    outputStream.writeObject(AuthenticationService.registerUser((SignupRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.LOGIN_REQUEST) {
                    System.out.println("Client wants to login!");
                    outputStream.writeObject(AuthenticationService.loginUser((LoginRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.VERIFY_USER_REQUEST) {
                    System.out.println("Client wants to verify his account!");
                    outputStream.writeObject(AuthenticationService.verifyUser((VerifyUserRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.GET_ME_REQUEST) {
                    System.out.println("Client wants to fetch his info!");
                    outputStream.writeObject(UserService.getUserData((GetMeRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.CREATE_CODEDOC_REQUEST) {
                    System.out.println("Client wants to create a CodeDoc!");
                    outputStream.writeObject(CodeDocService.createCodeDoc((CreateCodeDocRequest) request));
                    outputStream.flush();
                } else if (request.getRequestType() == RequestType.FETCH_CODEDOC_REQUEST) {
                    System.out.println("Client wants to fetch CodeDoc!");
                    outputStream.writeObject(CodeDocService.fetchCodeDoc((FetchCodeDocRequest) request));
                    outputStream.flush();
                }else if(request.getRequestType() == RequestType.DELETE_CODEDOC_REQUEST) {
                    System.out.println("Client wants to delete CodeDoc!");
                    outputStream.writeObject(CodeDocService.deleteCodeDoc((DeleteCodeDocRequest) request));
                    outputStream.flush();
                }else if(request.getRequestType() == RequestType.UPDATE_CODEDOC_REQUEST) {
                    System.out.println("Client wants to update CodeDoc details!");
                    outputStream.writeObject(CodeDocService.updateCodeDoc((UpdateCodeDocRequest) request));
                } else if (request.getRequestType() == RequestType.LOAD_EDITOR_REQUEST) {
                    System.out.println("Client wants to open editor!");
                    outputStream.writeObject(EditorService.loadEditor((LoadEditorRequest) request));
                    outputStream.flush();
                }else if (request.getRequestType() == RequestType.INVITE_COLLABORATOR_REQUEST) {
                    System.out.println("Client wants to invite a collaborator!");
                    outputStream.writeObject(CollaborationService.inviteCollaborator((InviteCollaboratorRequest) request));
                    outputStream.flush();
                }else if (request.getRequestType() == RequestType.ACCEPT_INVITE_REQUEST) {
                    System.out.println("Client wants to accept invite !");
                    outputStream.writeObject(CollaborationService.acceptInvite((AcceptInviteRequest) request));
                    outputStream.flush();
                }else if (request.getRequestType() == RequestType.REJECT_INVITE_REQUEST) {
                    System.out.println("Client wants to reject invite !");
                    outputStream.writeObject(CollaborationService.rejectInvite((RejectInviteRequest) request));
                    outputStream.flush();
                }else if (request.getRequestType() == RequestType.FETCH_INVITE_REQUEST) {
                    System.out.println("Client wants to fetch his/her invite !");
                    outputStream.writeObject(CollaborationService.fetchInvites( (FetchInviteRequest) request));
                    outputStream.flush();
                }else if (request.getRequestType() == RequestType.RUN_CODEDOC_REQUEST) {
                    System.out.println("Client wants to run codeDoc !");
                    outputStream.writeObject(CompileService.runCodeDoc( (RunCodeDocRequest) request));
                    outputStream.flush();
                }

            } catch (IOException | ClassNotFoundException e) {
                try {
                    inputStream.close();
                    outputStream.close();
                    client.close();
                } catch (IOException e1) {
                    System.out.println("Error: Unable to close connection!");
                    e1.printStackTrace();
                }
                System.out.println("Client disconnected!");
                break;
            }
        }
    }


}
