package models;

import java.io.Serializable;

public class CodeDoc implements Serializable {

    private String codeDocId;
    private String title;
    private String description;
    private String fileName;
    private String fileContent;
    private String ownerID;
    private String createdAt;
    private String updatedAt;
    private String language;

    public CodeDoc() {
    }

    public CodeDoc(String title, String description, String fileContent, String ownerID, String language) {
        this.title = title;
        this.description = description;
        this.fileContent = fileContent;
        this.ownerID = ownerID;
        this.language = language;
    }

    public CodeDoc(String codeDocId, String title, String description, String fileName, String fileContent, String ownerID, String language) {
        this.codeDocId = codeDocId;
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.ownerID = ownerID;
        this.language = language;
    }

    public String getCodeDocId() {
        return codeDocId;
    }

    public void setCodeDocId(String codeDocId) {
        this.codeDocId = codeDocId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
