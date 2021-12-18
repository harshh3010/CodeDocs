package services;

import mainClasses.CodeDocsClient;
import requests.appRequests.*;
import response.appResponse.*;
import utilities.CodeDocRequestType;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CollaborationService {

    private static final ObjectInputStream inputStream = CodeDocsClient.inputStream;
    private static final ObjectOutputStream outputStream = CodeDocsClient.outputStream;

    public static InviteCollaboratorResponse inviteCollaborator(String codeDocID, String receiverID,int writePermissions) throws IOException, ClassNotFoundException {

        InviteCollaboratorRequest request = new InviteCollaboratorRequest();
        request.setSenderID(UserApi.getInstance().getId());
        request.setReceiverID(receiverID);
        request.setCodeDocID(codeDocID);
        request.setWritePermissions(writePermissions);

        outputStream.writeObject(request);
        outputStream.flush();

        return (InviteCollaboratorResponse) inputStream.readObject();

    }

    public static FetchInviteResponse fetchInvites(int rowCount, int offset) throws IOException, ClassNotFoundException {

        FetchInviteRequest request = new FetchInviteRequest();
        request.setRowcount(rowCount);
        request.setOffset(offset);
        request.setUserID(UserApi.getInstance().getId());

        outputStream.writeObject(request);
        outputStream.flush();

        return (FetchInviteResponse) inputStream.readObject();
    }

    public static AcceptInviteResponse acceptInvite(String codeDocID) throws IOException, ClassNotFoundException {

        AcceptInviteRequest request = new AcceptInviteRequest();
        request.setCodeDocID(codeDocID);
        request.setReceiverID(UserApi.getInstance().getId());

        outputStream.writeObject(request);
        outputStream.flush();

        return (AcceptInviteResponse) inputStream.readObject();

    }

    public static RejectInviteResponse rejectInvite(String codeDocID) throws IOException, ClassNotFoundException {

        RejectInviteRequest request = new RejectInviteRequest();
        request.setCodeDocID(codeDocID);
        request.setReceiverID(UserApi.getInstance().getId());

        outputStream.writeObject(request);
        outputStream.flush();

        return (RejectInviteResponse) inputStream.readObject();

    }


}
