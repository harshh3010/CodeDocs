package services.clientServices;

import mainClasses.CodeDocsServer;
import requests.appRequests.CreateCodeDocRequest;
import response.appResponse.CreateCodeDocResponse;
import utilities.DatabaseConstants;
import utilities.Status;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

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
                    String fileName = "Solution." + createCodedocRequest.getCodeDoc().getLanguageType().getExtension();

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

}
