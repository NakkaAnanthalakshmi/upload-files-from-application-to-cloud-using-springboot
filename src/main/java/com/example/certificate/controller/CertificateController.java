package com.example.certificate.controller;

import com.example.certificate.model.Certificate;
import com.example.certificate.model.User;
import com.example.certificate.service.AwsS3Service;
import com.example.certificate.service.CertificateService;
import com.example.certificate.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "*")
public class CertificateController {

    private final AwsS3Service awsS3Service;
    private final CertificateService certificateService;
    private final UserService userService;

    public CertificateController(AwsS3Service awsS3Service, CertificateService certificateService, UserService userService) {
        this.awsS3Service = awsS3Service;
        this.certificateService = certificateService;
        this.userService = userService;
    }

    // Upload certificate and return QR code content
    @PostMapping("/upload")
    public Map<String, String> uploadCertificate(@RequestParam("file") MultipartFile file) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        byte[] bytes = file.getBytes();

        // Upload file to AWS S3, S3 key looks like certificates/{UUID}-filename
        String s3Key = "certificates/" + UUID.randomUUID() + "-" + filename;
        awsS3Service.uploadFile(s3Key, bytes, file.getContentType());

        // Store S3 key as both the fileUrl and qrCodeContent
        String qrCodeContent = s3Key;

        // Save the certificate record to DB
        Certificate cert = new Certificate(
            user.getId(),                    // userId
            filename,                        // fileName
            s3Key,                           // fileUrl
            new Date(),                      // uploadDate
            qrCodeContent                    // qrCodeContent (holds the S3 key)
        );
        certificateService.save(cert);

        Map<String, String> response = new HashMap<>();
        response.put("qrCodeContent", qrCodeContent);
        response.put("downloadUrl", awsS3Service.generatePresignedUrl(s3Key));
        return response;
    }

    // Generate QR code image for a given QR content
//    @GetMapping(value = "/qrcode/{qrCodeContent}", produces = MediaType.IMAGE_PNG_VALUE)
//    public @ResponseBody byte[] generateQrCode(@PathVariable String qrCodeContent) throws WriterException, IOException {
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 250, 250);
//
//        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
//        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
//        return pngOutputStream.toByteArray();
//    }
    @GetMapping(value = "/qrcode/{qrCodeContent:.+}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] generateQrCode(@PathVariable String qrCodeContent) throws WriterException, IOException {
        // Optionally print/log qrCodeContent here for debugging
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }


    // Download certificate from S3 by scanning QR code content sent as param
    @GetMapping("/download")
    public void downloadCertificate(@RequestParam("qrCodeContent") String qrCodeContent, HttpServletResponse response) throws IOException {
        Optional<Certificate> certOpt = certificateService.findByQrCodeContent(qrCodeContent);
        if (certOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Certificate not found");
            return;
        }
        Certificate certificate = certOpt.get();

        // Generate presigned URL for download/view (valid 15 min)
        String fileUrl = awsS3Service.generatePresignedUrl(certificate.getFileUrl());
        // Redirect to S3 pre-signed link
        response.sendRedirect(fileUrl);
    }

    // Show all certificates for the authenticated user only (with username/email from User)
    @GetMapping("/my-certificates")
    public List<Map<String, Object>> getMyCertificates(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        List<Certificate> certs = certificateService.findByUserId(user.getId());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Certificate cert : certs) {
            Map<String, Object> map = new HashMap<>();
            map.put("certificateName", cert.getFileName());
            map.put("qrCodeContent", cert.getQrCodeContent());
            map.put("username", user.getUsername());
            map.put("email", user.getEmail());
            result.add(map);
        }
        return result;
    }
}
