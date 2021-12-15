package services.clientServices;

import mainClasses.CodeDocsServer;
import models.CodeDoc;
import requests.appRequests.AcceptInviteRequest;
import requests.appRequests.FetchInviteRequest;
import requests.appRequests.InviteCollaboratorRequest;
import requests.appRequests.RejectInviteRequest;
import response.appResponse.AcceptInviteResponse;
import response.appResponse.FetchInviteResponse;
import response.appResponse.RejectInviteResponse;
import response.appResponse.InviteCollaboratorResponse;
import utilities.DatabaseConstants;
import utilities.Status;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollaborationService {


    public static InviteCollaboratorResponse inviteCollaborator(InviteCollaboratorRequest inviteCollaboratorRequest){

        InviteCollaboratorResponse inviteCollaboratorResponse = new InviteCollaboratorResponse();
        //sender cannot send request to himself
        if(inviteCollaboratorRequest.getSenderID() == inviteCollaboratorRequest.getReceiverID()){
            inviteCollaboratorResponse.setStatus(Status.FAILED);
            return inviteCollaboratorResponse;
        }
       //TODO: check performance
        String checkAccessRightQuery = "SELECT " +DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID +
                " FROM " + DatabaseConstants.CODEDOC_TABLE_NAME +
                " WHERE " + DatabaseConstants.CODEDOC_TABLE_COL_OWNERID + " =? " +
                " AND " + DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID + " =?";

        String insertInviteQuery = "INSERT INTO " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME
                + "(" + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID
                + "," + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID
                + "," + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT
                + "," + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_HAS_WRITE_PERMISSIONS
                + ") values(?,?,?,?);";
        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(checkAccessRightQuery);
                preparedStatement.setString(1, inviteCollaboratorRequest.getSenderID());
                preparedStatement.setString(2, inviteCollaboratorRequest.getCodeDocID());
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(insertInviteQuery);
                    preparedStatement.setString(1, inviteCollaboratorRequest.getCodeDocID());
                    preparedStatement.setString(2, inviteCollaboratorRequest.getReceiverID());
                    preparedStatement.setString(3, "PENDING");
                    preparedStatement.setInt(4, inviteCollaboratorRequest.getWritePermissions());
                    preparedStatement.executeUpdate();
                    CodeDocsServer.databaseConnection.commit();
                    inviteCollaboratorResponse.setStatus(Status.SUCCESS);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                CodeDocsServer.databaseConnection.rollback();
                inviteCollaboratorResponse.setStatus(Status.FAILED);
            }

        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return inviteCollaboratorResponse;
    }



    public static AcceptInviteResponse acceptInvite(AcceptInviteRequest acceptInviteRequest){
        AcceptInviteResponse acceptInviteResponse = new AcceptInviteResponse();

        String acceptInviteQuery = "UPDATE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " SET " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT + " =?" +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID+ " =?" +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID+ " =?";

        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(acceptInviteQuery);
                preparedStatement.setString(1, "COLLABORATOR");
                preparedStatement.setString(2, acceptInviteRequest.getCodeDocID());
                preparedStatement.setString(3, acceptInviteRequest.getReceiverID());
                preparedStatement.executeUpdate();
                CodeDocsServer.databaseConnection.commit();
                acceptInviteResponse.setStatus(Status.SUCCESS);
            } catch (SQLException e) {
                acceptInviteResponse.setStatus(Status.FAILED);
                CodeDocsServer.databaseConnection.rollback();
                e.printStackTrace();
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return acceptInviteResponse;
    }

    public static RejectInviteResponse rejectInvite(RejectInviteRequest rejectInviteRequest){

        RejectInviteResponse rejectInviteResponse= new RejectInviteResponse();

        String rejectInviteQuery = "DELETE FROM " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID + " =? " +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID+ " =?" +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT+ " =?";

        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(rejectInviteQuery);
                preparedStatement.setString(1, rejectInviteRequest.getCodeDocID());
                preparedStatement.setString(2, rejectInviteRequest.getReceiverID());
                preparedStatement.setString(3, "PENDING");
                preparedStatement.executeUpdate();
                CodeDocsServer.databaseConnection.commit();
                rejectInviteResponse.setStatus(Status.SUCCESS);
            } catch (SQLException e) {
                rejectInviteResponse.setStatus(Status.FAILED);
                CodeDocsServer.databaseConnection.rollback();
                e.printStackTrace();
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rejectInviteResponse;
    }

    public static FetchInviteResponse fetchInvites(FetchInviteRequest fetchInviteRequest){
        FetchInviteResponse fetchInviteResponse = new FetchInviteResponse();

        String fetchInvitesQuery = " SELECT " +
                DatabaseConstants.CODEDOC_TABLE_NAME + "." +DatabaseConstants.CODEDOC_TABLE_COL_TITLE+ ", " +
                DatabaseConstants.CODEDOC_TABLE_NAME+ "." +DatabaseConstants.CODEDOC_TABLE_COL_DESCRIPTION+ ", " +
                DatabaseConstants.USER_TABLE_NAME+ "." +DatabaseConstants.USER_TABLE_COL_FIRSTNAME +
                " FROM " +
                "(( " +DatabaseConstants.CODEDOC_TABLE_NAME +
                " INNER JOIN " +DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME+ " ON " +
                DatabaseConstants.CODEDOC_TABLE_NAME + "." +DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID+ "= " +
                DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME+ "." +DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID +
                " ) " +
                " INNER JOIN " +DatabaseConstants.USER_TABLE_NAME+ " ON " +
                DatabaseConstants.CODEDOC_TABLE_NAME + "." +DatabaseConstants.CODEDOC_TABLE_COL_OWNERID+ "= " +
                DatabaseConstants.USER_TABLE_NAME+ "." +DatabaseConstants.USER_TABLE_COL_USERID +
                " ) " +
                " WHERE " +
                DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME+"." + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID +"=?"+
                " AND " +
                DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME+"." + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT +"=?" +
                " ORDER BY " + DatabaseConstants.CODEDOC_TABLE_COL_UPDATED_AT + " DESC "
                + " LIMIT " + fetchInviteRequest.getOffset() +
                " , " + fetchInviteRequest.getRowcount()
                + ";";
        List<CodeDoc> inviteList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(fetchInvitesQuery);
            preparedStatement.setString(1, fetchInviteRequest.getUserID());
            preparedStatement.setString(2, "PENDING");

            ResultSet resultSet = preparedStatement.executeQuery();
            CodeDoc invite = null;

            while (resultSet.next()) {
                invite = new CodeDoc();
                invite.setTitle(resultSet.getString(1));
                invite.setDescription(resultSet.getString(2));
                invite.setOwnerName(resultSet.getString(3));
                inviteList.add(invite);
            }
            fetchInviteResponse.setInvites(inviteList);
            fetchInviteResponse.setStatus(Status.SUCCESS);

        } catch (SQLException e) {
            fetchInviteResponse.setStatus(Status.FAILED);
            e.printStackTrace();
        }
        return fetchInviteResponse;
    }
}
