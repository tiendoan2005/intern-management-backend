package com.example.backend.service.task;

import com.example.backend.dto.task.CreateTaskRequest;
import com.example.backend.dto.task.TaskDTO;
import com.example.backend.dto.task.UpdateTaskRequest;
import com.example.backend.dto.task.UpdateTaskStatusRequest;
import com.example.backend.entity.ProgramGroup;
import com.example.backend.entity.Task;
import com.example.backend.entity.User;
import com.example.backend.enums.TaskStatus;
import com.example.backend.repository.GroupMemberRepository;
import com.example.backend.repository.InternProfileRepository;
import com.example.backend.repository.ProgramGroupRepository;
import com.example.backend.repository.TaskRepository;
import com.example.backend.util.RoleUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProgramGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final InternProfileRepository internProfileRepository;

    public TaskDTO create(CreateTaskRequest req, User currentUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        ProgramGroup group = groupRepository.findById(req.groupId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        // mentor/hr/admin only
        if (!RoleUtils.isAdminOrHr(auth) && !RoleUtils.isMentor(auth)) {
            throw new AccessDeniedException("Only mentor/hr/admin can create tasks");
        }

        // mentor must own group
        if (RoleUtils.isMentor(auth)) {
            ensureMentorOwnsGroup(group, currentUser);
        }

        Task t = new Task();
        t.setGroup(group);
        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setDueDate(req.dueDate());
        t.setStatus(TaskStatus.OPEN);
        t.setCreatedBy(currentUser);

        return TaskMapper.toDTO(taskRepository.save(t));
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByGroup(Long groupId, User currentUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        ProgramGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        // Admin/HR: ok
        if (RoleUtils.isAdminOrHr(auth)) {
            return taskRepository.findByGroup_IdOrderByCreatedAtDesc(groupId)
                    .stream().map(TaskMapper::toDTO).toList();
        }

        // Mentor: must own group
        if (RoleUtils.isMentor(auth)) {
            ensureMentorOwnsGroup(group, currentUser);
            return taskRepository.findByGroup_IdOrderByCreatedAtDesc(groupId)
                    .stream().map(TaskMapper::toDTO).toList();
        }

        // Intern: must be active member of group
        if (RoleUtils.isIntern(auth)) {
            var intern = internProfileRepository.findByUser_Id(currentUser.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Intern profile not found"));

            if (!memberRepository.isInternActiveInGroup(intern.getId(), groupId)) {
                throw new AccessDeniedException("Not a member of this group");
            }

            return taskRepository.findByGroup_IdOrderByCreatedAtDesc(groupId)
                    .stream().map(TaskMapper::toDTO).toList();
        }

        throw new AccessDeniedException("No permission");
    }

    /**
     * Intern: view my tasks (optionally filter by status)
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getMyTasks(TaskStatus status, User currentUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!RoleUtils.isIntern(auth)) {
            throw new AccessDeniedException("Only intern");
        }

        var intern = internProfileRepository.findByUser_Id(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Intern profile not found"));

        List<Long> groupIds = memberRepository.findActiveGroupIdsByIntern(intern.getId());
        if (groupIds.isEmpty()) return List.of();

        var tasks = (status == null)
                ? taskRepository.findByGroup_IdInOrderByCreatedAtDesc(groupIds)
                : taskRepository.findByGroup_IdInAndStatusOrderByCreatedAtDesc(groupIds, status);

        return tasks.stream().map(TaskMapper::toDTO).toList();
    }

    /**
     * Mentor/Admin/HR update task
     */
    public TaskDTO update(Long taskId, UpdateTaskRequest req, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        ensureMentorOwnsTaskOrAdminHr(task, currentUser);

        task.setTitle(req.title());
        task.setDescription(req.description());
        task.setDueDate(req.dueDate());

        return TaskMapper.toDTO(taskRepository.save(task));
    }

    /**
     * Mentor/Admin/HR delete task
     */
    public void delete(Long taskId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        ensureMentorOwnsTaskOrAdminHr(task, currentUser);

        taskRepository.delete(task);
    }

    /**
     * Mentor/Admin/HR: set freely
     * Intern: rule OPEN -> IN_PROGRESS -> DONE and must be member of task.group
     */
    public TaskDTO updateStatus(Long taskId, UpdateTaskStatusRequest req, User currentUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        // Mentor/Admin/HR: set freely
        if (RoleUtils.isAdminOrHr(auth) || RoleUtils.isMentor(auth)) {
            if (RoleUtils.isMentor(auth)) {
                ensureMentorOwnsTaskOrAdminHr(task, currentUser);
            }
            task.setStatus(req.status());
            return TaskMapper.toDTO(taskRepository.save(task));
        }

        // Intern: must be member + rule (OPEN -> IN_PROGRESS -> DONE)
        if (RoleUtils.isIntern(auth)) {
            var intern = internProfileRepository.findByUser_Id(currentUser.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Intern profile not found"));

            if (!memberRepository.isInternActiveInGroup(intern.getId(), task.getGroup().getId())) {
                throw new AccessDeniedException("Not a member of this task's group");
            }

            TaskStatus current = task.getStatus();
            TaskStatus target = req.status();

            boolean ok =
                    (current == TaskStatus.OPEN && target == TaskStatus.IN_PROGRESS) ||
                            (current == TaskStatus.IN_PROGRESS && target == TaskStatus.DONE);

            if (!ok) throw new AccessDeniedException("Invalid status transition for intern");

            task.setStatus(target);
            return TaskMapper.toDTO(taskRepository.save(task));
        }

        throw new AccessDeniedException("No permission");
    }

    // ===== Helpers =====

    private void ensureMentorOwnsTaskOrAdminHr(Task task, User currentUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (RoleUtils.isAdminOrHr(auth)) return;
        if (!RoleUtils.isMentor(auth)) throw new AccessDeniedException("Only mentor/hr/admin can do this");

        ProgramGroup group = task.getGroup();
        ensureMentorOwnsGroup(group, currentUser);
    }

    private void ensureMentorOwnsGroup(ProgramGroup group, User currentUser) {
        Long mentorUserId = (group.getMentor() != null && group.getMentor().getUser() != null)
                ? group.getMentor().getUser().getId()
                : null;

        if (mentorUserId == null || !mentorUserId.equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not mentor of this group");
        }
    }
}
