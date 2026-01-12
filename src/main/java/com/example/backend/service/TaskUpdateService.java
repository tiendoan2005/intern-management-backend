package com.example.backend.service;

import com.example.backend.dto.taskupdate.CreateTaskUpdateRequest;
import com.example.backend.dto.taskupdate.TaskUpdateDTO;
import com.example.backend.entity.InternProfile;
import com.example.backend.entity.Task;
import com.example.backend.entity.TaskUpdate;
import com.example.backend.entity.User;
import com.example.backend.repository.InternProfileRepository;
import com.example.backend.repository.TaskRepository;
import com.example.backend.repository.TaskUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskUpdateService {

    private final TaskUpdateRepository taskUpdateRepository;
    private final TaskRepository taskRepository;
    private final InternProfileRepository internProfileRepository;

    /* =====================================================
       INTERN ADD UPDATE
       ===================================================== */
    @Transactional
    public TaskUpdateDTO addUpdate(Long taskId, CreateTaskUpdateRequest req, User currentUser) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        // User -> InternProfile (vì User không có getInternProfile())
        InternProfile intern = internProfileRepository.findByUser_Id(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Intern profile not found for userId: " + currentUser.getId()));

        TaskUpdate update = new TaskUpdate();
        update.setTask(task);
        update.setIntern(intern);

        update.setProgressPercent(req.getProgressPercent());
        update.setContent(req.getContent());

        // NOTE: Entity TaskUpdate hiện chưa có attachments/mentorFeedback/feedbackAt
        // nên chỉ lưu được progressPercent + content.

        TaskUpdate saved = taskUpdateRepository.save(update);
        return toDTO(saved);
    }

    /* =====================================================
       LIST UPDATES
       ===================================================== */
    @Transactional(readOnly = true)
    public List<TaskUpdateDTO> listUpdates(Long taskId, User currentUser) {

        taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        // Repo của bạn đang là DESC
        return taskUpdateRepository.findByTask_IdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /* =====================================================
       ENTITY → DTO
       ===================================================== */
    private TaskUpdateDTO toDTO(TaskUpdate u) {
        TaskUpdateDTO dto = new TaskUpdateDTO();

        dto.setId(u.getId());
        dto.setTaskId(u.getTask().getId());

        if (u.getIntern() != null && u.getIntern().getUser() != null) {
            dto.setInternId(u.getIntern().getId());
            dto.setInternName(u.getIntern().getUser().getFullName());
            dto.setInternEmail(u.getIntern().getUser().getEmail());
        }

        dto.setProgressPercent(u.getProgressPercent());
        dto.setContent(u.getContent());

        // Vì entity chưa support, trả về rỗng/null cho đúng thực tế
        dto.setAttachments(Collections.emptyList());
        dto.setMentorFeedback(null);
        dto.setFeedbackAt(null);

        dto.setCreatedAt(u.getCreatedAt());
        return dto;
    }
}
