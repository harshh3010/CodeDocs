import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection extends Thread{

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

        // TODO: Accept client requests

        try {
            inputStream.close();
            outputStream.close();
            client.close();
        } catch (IOException e) {
            System.out.println("Error: Unable to close connection!");
            e.printStackTrace();
        }
    }


}
