package com.example.certificate.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "certificates")
public class Certificate {

    @Id
    private String id;

    private String userId;

    private String fileName;

    private String fileUrl; // S3 URL link

    private Date uploadDate;

    private String qrCodeContent;

    // constructors, getters, and setters

    public Certificate() {}

    public Certificate(String userId, String fileName, String fileUrl, Date uploadDate, String qrCodeContent) {
        this.userId = userId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.uploadDate = uploadDate;
        this.qrCodeContent = qrCodeContent;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public Date getUploadDate() { return uploadDate; }
    public void setUploadDate(Date uploadDate) { this.uploadDate = uploadDate; }

    public String getQrCodeContent() { return qrCodeContent; }
    public void setQrCodeContent(String qrCodeContent) { this.qrCodeContent = qrCodeContent; }
}
