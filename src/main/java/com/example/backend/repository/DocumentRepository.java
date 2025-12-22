package com.example.backend.repository;

import com.example.backend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository  extends JpaRepository<Document, Integer> {
    List<Document> findByOwner_Id(Long ownerId);
}
