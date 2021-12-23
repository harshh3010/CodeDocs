package utilities.configurations;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ServerConfigurations {

    private final int port;
    private final String hostname;

    public ServerConfigurations() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader("CodeDocs-Client/src/main/resources/configurations/server.properties");
        properties.load(fileReader);

        hostname = properties.getProperty("HOSTNAME");
        port = Integer.parseInt(properties.getProperty("PORT"));
    }

    public int getPort() {
        return port;
    }

    public String getHostname() {
        return hostname;
    }
}
