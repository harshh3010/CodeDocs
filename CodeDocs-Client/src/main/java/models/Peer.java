package models;

import java.io.*;

public class Peer implements Serializable {

    private User user;
    private boolean hasWritePermissions;
    private String ipAddress;
    private boolean isMuted;
    private int port;
    private int audioPort;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private DataInputStream audioInputStream;
    private DataOutputStream audioOutputStream;

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public DataInputStream getAudioInputStream() {
        return audioInputStream;
    }

    public void setAudioInputStream(DataInputStream audioInputStream) {
        this.audioInputStream = audioInputStream;
    }

    public DataOutputStream getAudioOutputStream() {
        return audioOutputStream;
    }

    public void setAudioOutputStream(DataOutputStream audioOutputStream) {
        this.audioOutputStream = audioOutputStream;
    }

    public int getAudioPort() {
        return audioPort;
    }

    public void setAudioPort(int audioPort) {
        this.audioPort = audioPort;
    }

    public boolean isHasWritePermissions() {
        return hasWritePermissions;
    }

    public void setHasWritePermissions(boolean hasWritePermissions) {
        this.hasWritePermissions = hasWritePermissions;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
