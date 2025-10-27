package com.example.certificate.service;

import com.example.certificate.model.Certificate;
import com.example.certificate.repository.CertificateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    public Certificate save(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public Optional<Certificate> findByQrCodeContent(String qrCode) {
        return certificateRepository.findByQrCodeContent(qrCode);
    }
    public List<Certificate> findByUserId(String userId) {
        return certificateRepository.findByUserId(userId);
    }

}
