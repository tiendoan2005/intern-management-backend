// ProgramGroupRepository.java
package com.example.backend.repository;

import com.example.backend.entity.ProgramGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProgramGroupRepository extends JpaRepository<ProgramGroup, Long> {
    @Query("select g.id from ProgramGroup g where g.mentor.id = :mentorId")
    List<Long> findGroupIdsByMentorId(@Param("mentorId") Long mentorId);
}
