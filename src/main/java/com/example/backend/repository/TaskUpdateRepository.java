package com.example.backend.repository;

import com.example.backend.entity.TaskUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskUpdateRepository extends JpaRepository<TaskUpdate, Long> {

    List<TaskUpdate> findByTask_IdOrderByCreatedAtAsc(Long taskId);
    boolean existsByTask_IdAndIntern_Id(Long taskId, Long internId);
}
