package com.example.certificate.repository;

import com.example.certificate.model.Certificate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends MongoRepository<Certificate, String> {
    Optional<Certificate> findByQrCodeContent(String qrCodeContent);
    List<Certificate> findByUserId(String userId);

}
