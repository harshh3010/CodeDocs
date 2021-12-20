package mainClasses.audioConnection;

import mainClasses.EditorConnection;

import javax.sound.sampled.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AudioReceiverConnection extends Thread {

    private final Socket connection;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final EditorConnection editorConnection;

    public AudioReceiverConnection(Socket connection, EditorConnection editorConnection) throws IOException {
        this.editorConnection = editorConnection;

        this.connection = connection;
        this.outputStream = new DataOutputStream(connection.getOutputStream());
        this.inputStream = new DataInputStream(connection.getInputStream());
    }

    @Override
    public void run() {

        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);

        try {
            SourceDataLine speakers;

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speakers.open(format);
            speakers.start();

            int CHUNK_SIZE = 1024;
            byte[] data = new byte[CHUNK_SIZE];
            int numBytesRead;
            while (true) {
                numBytesRead = inputStream.read(data, 0, CHUNK_SIZE);
                speakers.write(data, 0, numBytesRead);
            }
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }

        try {
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
