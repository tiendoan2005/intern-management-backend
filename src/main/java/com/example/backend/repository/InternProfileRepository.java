package com.example.backend.repository;

import com.example.backend.entity.InternProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternProfileRepository extends JpaRepository<InternProfile, Integer> {
}
