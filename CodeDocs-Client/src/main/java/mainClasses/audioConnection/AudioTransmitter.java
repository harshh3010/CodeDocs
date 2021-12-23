package mainClasses.audioConnection;

import mainClasses.EditorConnection;
import models.Peer;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class AudioTransmitter extends Thread {

    private final EditorConnection editorConnection;

    public AudioTransmitter(EditorConnection editorConnection) {
        this.editorConnection = editorConnection;
    }

    @Override
    public void run() {

        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);

        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            int numBytesRead;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();

            while (true) {
                try {
                    numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                    if(!editorConnection.isMute()){
                        for (Peer peer : editorConnection.getConnectedPeers().values()) {
                            peer.getAudioOutputStream().write(data, 0, numBytesRead);
                            peer.getAudioOutputStream().flush();
                        }
                    }
                } catch (IOException e) {
//                    System.out.println("Could not transmit audio to a peer!");
                }
            }

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
