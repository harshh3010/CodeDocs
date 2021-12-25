package utilities;

import java.io.*;

public class CompileUtility {

    //utility to read stream and return the corresponding result
    public static String getStreamResult(InputStream ins) throws Exception {
        String line = null;
        String streamText ="";

        BufferedReader buffer = new BufferedReader( new InputStreamReader(ins));

        while ((line = buffer.readLine()) != null) {
            streamText = streamText + "\n" + line;
        }
        buffer.close();
        return streamText;
    }

    // utility used to run a program ... It executes specified command using process class
    //  writes input to the outputStream and returns error/result (if any)
    public static ExecuteResponse runProcess(String command, String inputArguments) throws Exception {

        Process p = Runtime.getRuntime().exec("cmd /c "+command);
        OutputStream outputStream = p.getOutputStream ();
        byte [] bytes = inputArguments.getBytes ();
        outputStream.write (bytes);
        outputStream.close ();
        p.waitFor ();

        ExecuteResponse response = new ExecuteResponse();
        response.setError(getStreamResult( p.getErrorStream()));
        response.setOutput(getStreamResult( p.getInputStream()));

        return response;

    }

    // utility used to compile ... It executes specified command using process class
    // and returns error if it occurs
    public static ExecuteResponse compileProcess(String command) throws Exception {

        Process p = Runtime.getRuntime().exec("cmd /c "+command);
        p.waitFor();
        ExecuteResponse response = new ExecuteResponse();
        response.setError(getStreamResult( p.getErrorStream()));
        return response;

    }
}