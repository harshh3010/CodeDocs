package services.clientServices;

import mainClasses.CodeDocsServer;
import requests.editorRequests.CompileCodeDocRequest;
import requests.editorRequests.RunCodeDocRequest;
import response.editorResponse.CompileCodeDocResponse;
import response.editorResponse.RunCodeDocResponse;
import utilities.CompileUtility;
import utilities.DatabaseConstants;
import utilities.ExecuteResponse;
import utilities.Status;

import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class CompileService {

    public static String compileCommand[] = {
            "javac ./Solution.java",
            "python -m compileall ./Solution.py",
            "gcc Solution.c -o Solution.exe",
            "g++ Solution.cpp -o Solution.exe"
    };

    public static String runCommand[] = {
            "java Solution",
            "python Solution.pyc",
            "Solution.exe",
            "Solution.exe"
    };


    public static RunCodeDocResponse runCodeDoc(RunCodeDocRequest runCodeDocRequest) {

        RunCodeDocResponse runCodeDocResponse = new RunCodeDocResponse();

        String checkAccessQuery = " SELECT * " +
                " FROM " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID + " =?" +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID + " =?" +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT + "!=?;";
        try {
            PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(checkAccessQuery);
            preparedStatement.setString(1, runCodeDocRequest.getCodeDocID());
            preparedStatement.setString(2, runCodeDocRequest.getUserID());
            preparedStatement.setString(3, "PENDING");
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                Properties properties = new Properties();
                FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/db.properties");
                properties.load(fileReader);
                String filePath = properties.getProperty("FILEPATH");
                filePath += runCodeDocRequest.getCodeDocID() + "\\";

                int index = runCodeDocRequest.getLanguageType().getIndex();

                ExecuteResponse executeResponse = CompileUtility.runProcess(
                        "cd /d " + filePath
                                + " && " + compileCommand[index]
                                + " && " + runCommand[index],
                        runCodeDocRequest.getInput()
                );

                runCodeDocResponse.setOutput(executeResponse.getOutput());
                runCodeDocResponse.setError(executeResponse.getError());
                runCodeDocResponse.setStatus(Status.SUCCESS);
                return runCodeDocResponse;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        runCodeDocResponse.setOutput("");
        runCodeDocResponse.setError("");
        runCodeDocResponse.setStatus(Status.FAILED);

        return runCodeDocResponse;
    }

    public static CompileCodeDocResponse compileCodeDoc(CompileCodeDocRequest request) {

        CompileCodeDocResponse response = new CompileCodeDocResponse();

        String checkAccessQuery = " SELECT * " +
                " FROM " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID + " =?" +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID + " =?" +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT + "!=?;";
        try {

            PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(checkAccessQuery);
            preparedStatement.setString(1, request.getCodeDocID());
            preparedStatement.setString(2, request.getUserID());
            preparedStatement.setString(3, "PENDING");
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                Properties properties = new Properties();
                FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/db.properties");
                properties.load(fileReader);
                String filePath = properties.getProperty("FILEPATH");
                filePath += request.getCodeDocID() + "\\";

                int index = request.getLanguageType().getIndex();

                ExecuteResponse executeResponse = CompileUtility.compileProcess(
                        "cd /d " + filePath
                                + " && " + compileCommand[index]
                );
                response.setError(executeResponse.getError());
                response.setStatus(Status.SUCCESS);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(Status.FAILED);
        }
        return response;
    }
}
