package services.clientServices;

import mainClasses.CodeDocsServer;
import models.CodeDoc;
import requests.appRequests.CreateCodeDocRequest;
import requests.appRequests.FetchCodeDocRequest;
import response.appResponse.CreateCodeDocResponse;
import response.appResponse.FetchCodeDocResponse;
import utilities.CodeDocRequestType;
import utilities.DatabaseConstants;
import utilities.LanguageType;
import utilities.Status;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CodeDocService {

    public static CreateCodeDocResponse createCodeDoc(CreateCodeDocRequest createCodedocRequest) {

        CreateCodeDocResponse codedocResponse = new CreateCodeDocResponse();

        // create a code-doc file
        String codeDocID = UUID.randomUUID().toString();

        String codeDocQuery = "INSERT INTO " + DatabaseConstants.CODEDOC_TABLE_NAME
                + "(" + DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID
                + "," + DatabaseConstants.CODEDOC_TABLE_COL_TITLE
                + "," + DatabaseConstants.CODEDOC_TABLE_COL_DESCRIPTION
                + "," + DatabaseConstants.CODEDOC_TABLE_COL_LANGUAGE
                + "," + DatabaseConstants.CODEDOC_TABLE_COL_OWNERID
                + ") values(?,?,?,?,?);";

        String accessQuery = "INSERT INTO " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME
                + "(" + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID
                + "," + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID
                + "," + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_IS_OWNER
                + "," + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_HAS_WRITE_PERMISSIONS
                + ") values(?,?,?,?);";

        try {

            CodeDocsServer.databaseConnection.setAutoCommit(false);

            try {

                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(codeDocQuery);
                preparedStatement.setString(1, codeDocID);
                preparedStatement.setString(2, createCodedocRequest.getCodeDoc().getTitle());
                preparedStatement.setString(3, createCodedocRequest.getCodeDoc().getDescription());
                preparedStatement.setString(4, createCodedocRequest.getCodeDoc().getLanguageType().getLanguage());
                preparedStatement.setString(5, createCodedocRequest.getCodeDoc().getOwnerID());
                preparedStatement.executeUpdate();

                preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(accessQuery);
                preparedStatement.setString(1, codeDocID);
                preparedStatement.setString(2, createCodedocRequest.getCodeDoc().getOwnerID());
                preparedStatement.setInt(3, 1);
                preparedStatement.setInt(4, 1);
                preparedStatement.executeUpdate();

                Properties properties = new Properties();
                FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/db.properties");
                properties.load(fileReader);
                String filePath = properties.getProperty("FILEPATH");

                filePath += codeDocID;
                File codeDocDirectory = new File(filePath);

                if (codeDocDirectory.mkdir()) {

                    filePath += "/";
                    String fileName = "Solution" + createCodedocRequest.getCodeDoc().getLanguageType().getExtension();

                    BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath + fileName));
                    fileWriter.write(createCodedocRequest.getCodeDoc().getFileContent());
                    fileWriter.close();

                    CodeDocsServer.databaseConnection.commit();

                    codedocResponse.setStatus(Status.SUCCESS);
                    codedocResponse.setCodeDocId(codeDocID);
                    codedocResponse.setCreatedAt(new Date());
                    codedocResponse.setUpdatedAt(new Date());

                    return codedocResponse;
                } else {
                    CodeDocsServer.databaseConnection.rollback();
                    System.out.println("Unable to create CodeDoc directory!");
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        codedocResponse.setStatus(Status.FAILED);
        return codedocResponse;
    }


    public static FetchCodeDocResponse fetchCodeDoc(FetchCodeDocRequest fetchCodeDocRequest) {

        FetchCodeDocResponse fetchCodeDocResponse = new FetchCodeDocResponse();
        //set this in order to know that it is response corr to which type of codeDoc fetch req
        fetchCodeDocResponse.setCodeDocRequestType(fetchCodeDocRequest.getCodeDocRequestType());
        String condition, qualifier;
        if (fetchCodeDocRequest.getCodeDocRequestType() == CodeDocRequestType.FETCH_A_CODEDOC) {
            condition = DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID + " = ? ";
            qualifier = fetchCodeDocRequest.getCodeDocID();
        } else if (fetchCodeDocRequest.getCodeDocRequestType() == CodeDocRequestType.PERSONAL_CODEDOCS) {
            condition = DatabaseConstants.CODEDOC_TABLE_COL_OWNERID + " = ? ";
            qualifier = fetchCodeDocRequest.getUserID();
        } else {
            condition = " " + DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID +
                    " IN ( SELECT " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID +
                    " FROM " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                    " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID + " = ?) ";
            qualifier = fetchCodeDocRequest.getUserID();
        }

        String query = "SELECT " +
                " " + DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID +
                ", " + DatabaseConstants.CODEDOC_TABLE_COL_TITLE +
                ", " + DatabaseConstants.CODEDOC_TABLE_COL_DESCRIPTION +
                ", " + DatabaseConstants.CODEDOC_TABLE_COL_LANGUAGE +
                ", " + DatabaseConstants.CODEDOC_TABLE_COL_UPDATED_AT +
                ", " + DatabaseConstants.CODEDOC_TABLE_COL_CREATED_AT +
                ", " + DatabaseConstants.CODEDOC_TABLE_COL_OWNERID +
                " FROM " + DatabaseConstants.CODEDOC_TABLE_NAME
                + " WHERE " + condition
                + " ORDER BY " + DatabaseConstants.CODEDOC_TABLE_COL_UPDATED_AT + " DESC "
                + " LIMIT " + fetchCodeDocRequest.getOffset() +
                " , " + fetchCodeDocRequest.getRowcount()
                + ";";


        List<CodeDoc> codeDocList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(query);

            preparedStatement.setString(1, qualifier);
            ResultSet resultSet = preparedStatement.executeQuery();
            CodeDoc codeDoc = null;

            while (resultSet.next()) {
                codeDoc = new CodeDoc();
                codeDoc.setCodeDocId(resultSet.getString(1));
                codeDoc.setTitle(resultSet.getString(2));
                codeDoc.setDescription(resultSet.getString(3));
                codeDoc.setCreatedAt(resultSet.getDate(6));
                codeDoc.setUpdatedAt(resultSet.getDate(5));
                codeDoc.setOwnerID(resultSet.getString(7));
                codeDoc.setLanguageType(LanguageType.valueOf(resultSet.getString(4)));
                codeDocList.add(codeDoc);
            }
            fetchCodeDocResponse.setCodeDocs(codeDocList);
            fetchCodeDocResponse.setStatus(Status.SUCCESS);
            return fetchCodeDocResponse;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fetchCodeDocResponse.setStatus(Status.FAILED);
        return fetchCodeDocResponse;
    }


}
