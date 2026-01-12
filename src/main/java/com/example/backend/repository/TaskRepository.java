package com.example.backend.repository;

import com.example.backend.entity.Task;
import com.example.backend.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByGroup_IdOrderByCreatedAtDesc(Long groupId);
    List<Task> findByGroup_IdInOrderByCreatedAtDesc(Collection<Long> groupIds);
    List<Task> findByGroup_IdInAndStatusOrderByCreatedAtDesc(Collection<Long> groupIds, TaskStatus status);
}
