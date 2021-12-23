package models;

import java.io.Serializable;
import java.util.Date;

public class Screenshot implements Serializable {

    private String title;
    private Date createdAt;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
