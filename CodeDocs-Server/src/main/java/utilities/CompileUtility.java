package utilities;

import java.io.*;

public class CompileUtility {

    public static String printLines(String name, InputStream ins) throws Exception {
        String line = null;
        String str ="";
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            str = str + "\n" + line;
            System.out.println(  line);
        }
        return str;
    }



    public static ExecuteResponse runProcess(String command) throws Exception {

        /**ExecuteResponse response = new ExecuteResponse();
         Process pro = Runtime.getRuntime().exec("cmd /c "+command);
         OutputStream stdin = pro.getOutputStream ();
         response.setOutput(printLines(command + " stdout:", pro.getInputStream()));
         response.setError(printLines(command + " stderr:", pro.getErrorStream()));

         pro.waitFor();
         response.setExitStatus(pro.exitValue());
         return response;*/
        Process p = Runtime.getRuntime().exec("cmd /c "+command);
        p.waitFor();

        String line;
        System.out.println("***");
        OutputStream outputStream = p.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println();
        printStream.flush();
        printStream.close();
        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String errorText="";
        String outputText="";
        while((line = error.readLine()) != null){
            errorText+=line;
            System.out.println(line);
        }
        error.close();

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while((line=input.readLine()) != null){
            outputText+=line;
            System.out.println(line);
        }

        input.close();


        ExecuteResponse response = new ExecuteResponse();
        response.setError(errorText);
        response.setOutput(outputText);
        return response;

    }
}