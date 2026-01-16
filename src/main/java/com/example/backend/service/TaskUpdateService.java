package com.example.backend.service;

import com.example.backend.dto.taskupdate.CreateTaskUpdateRequest;
import com.example.backend.dto.taskupdate.TaskUpdateDTO;
import com.example.backend.entity.InternProfile;
import com.example.backend.entity.Task;
import com.example.backend.entity.TaskUpdate;
import com.example.backend.entity.User;
import com.example.backend.enums.TaskStatus;
import com.example.backend.repository.TaskRepository;
import com.example.backend.repository.TaskUpdateRepository;
import com.example.backend.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskUpdateService {

    private final TaskRepository taskRepository;
    private final TaskUpdateRepository taskUpdateRepository;
    private final NotificationService notificationService;

    @Transactional
    public TaskUpdateDTO addUpdate(Long taskId, CreateTaskUpdateRequest req, User current) {

        // ✅ Không dùng user.getRoles() nữa (tránh LazyInitializationException)
        // Controller đã @PreAuthorize("hasRole('INTERN')") nhưng mình vẫn check lại bằng authorities cho chắc.
        requireRole("INTERN");

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + taskId));

        InternProfile intern = current.getInternProfile();
        if (intern == null) {
            throw new AccessDeniedException("Intern profile not found");
        }

        Integer progress = req.getProgressPercent();
        if (progress == null || progress < 0 || progress > 100) {
            throw new IllegalArgumentException("progressPercent must be between 0 and 100");
        }

        TaskUpdate update = new TaskUpdate();
        update.setTask(task);
        update.setIntern(intern);
        update.setProgressPercent(progress);
        update.setContent(req.getContent());

        update = taskUpdateRepository.save(update);

        if (progress >= 100) {
            task.setStatus(TaskStatus.DONE);
        } else if (progress > 0) {
            if (task.getStatus() == null || task.getStatus() == TaskStatus.OPEN) {
                task.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
        taskRepository.save(task);

        notificationService.notifyMentorTaskUpdated(task, intern, update);

        return toDTO(update);
    }

    @Transactional(readOnly = true)
    public List<TaskUpdateDTO> listUpdates(Long taskId, User current) {

        taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + taskId));

        // ✅ Phân quyền bằng authorities thay vì user.getRoles()
        boolean isIntern = hasAuthority("ROLE_INTERN");
        boolean isPrivileged = hasAuthority("ROLE_ADMIN") || hasAuthority("ROLE_HR") || hasAuthority("ROLE_MENTOR");

        if (isIntern) {
            InternProfile intern = current.getInternProfile();
            if (intern == null) throw new AccessDeniedException("Intern profile not found");

            boolean ok = taskUpdateRepository.existsByTask_IdAndIntern_Id(taskId, intern.getId());
            if (!ok) {
                throw new AccessDeniedException("You don't have permission to view this task updates");
            }
        } else if (!isPrivileged) {
            throw new AccessDeniedException("You don't have permission to view this task updates");
        }

        return taskUpdateRepository.findByTask_IdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private TaskUpdateDTO toDTO(TaskUpdate u) {
        String internName = null;
        if (u.getIntern() != null && u.getIntern().getUser() != null) {
            internName = u.getIntern().getUser().getFullName();
        }

        return TaskUpdateDTO.builder()
                .id(u.getId())
                .taskId(u.getTask() != null ? u.getTask().getId() : null)
                .internId(u.getIntern() != null ? u.getIntern().getId() : null)
                .internName(internName)
                .progressPercent(u.getProgressPercent())
                .content(u.getContent())
                .createdAt(u.getCreatedAt())
                .build();
    }

    // ===== AUTHORITY HELPERS (không đụng DB) =====

    private void requireRole(String role) {
        if (!hasAuthority("ROLE_" + role)) {
            throw new AccessDeniedException("Required role: " + role);
        }
    }

    private boolean hasAuthority(String authority) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> authority.equals(a.getAuthority()));
    }
}
