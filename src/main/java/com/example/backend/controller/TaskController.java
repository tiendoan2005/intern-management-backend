package com.example.backend.controller;

import com.example.backend.dto.task.CreateTaskRequest;
import com.example.backend.dto.task.TaskDTO;
import com.example.backend.dto.task.UpdateTaskRequest;
import com.example.backend.dto.task.UpdateTaskStatusRequest;

import com.example.backend.dto.taskupdate.CreateTaskUpdateRequest;
import com.example.backend.dto.taskupdate.TaskUpdateDTO;

import com.example.backend.enums.TaskStatus;
import com.example.backend.security.CurrentUserProvider;
import com.example.backend.service.TaskUpdateService;
import com.example.backend.service.task.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskUpdateService updateService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','MENTOR')")
    public ResponseEntity<TaskDTO> create(@Valid @RequestBody CreateTaskRequest req) {
        var user = currentUserProvider.requireUser();
        return ResponseEntity.ok(taskService.create(req, user));
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskDTO>> getByGroup(@PathVariable Long groupId) {
        var user = currentUserProvider.requireUser();
        return ResponseEntity.ok(taskService.getTasksByGroup(groupId, user));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<List<TaskDTO>> myTasks(@RequestParam(required = false) TaskStatus status) {
        var user = currentUserProvider.requireUser();
        return ResponseEntity.ok(taskService.getMyTasks(status, user));
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MENTOR')")
    public ResponseEntity<TaskDTO> update(@PathVariable Long taskId, @Valid @RequestBody UpdateTaskRequest req) {
        var user = currentUserProvider.requireUser();
        return ResponseEntity.ok(taskService.update(taskId, req, user));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MENTOR')")
    public ResponseEntity<Void> delete(@PathVariable Long taskId) {
        var user = currentUserProvider.requireUser();
        taskService.delete(taskId, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MENTOR','INTERN')")
    public ResponseEntity<TaskDTO> updateStatus(@PathVariable Long taskId, @Valid @RequestBody UpdateTaskStatusRequest req) {
        var user = currentUserProvider.requireUser();
        return ResponseEntity.ok(taskService.updateStatus(taskId, req, user));
    }

    @PostMapping("/{taskId}/updates")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<TaskUpdateDTO> addUpdate(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateTaskUpdateRequest req
    ) {
        var user = currentUserProvider.requireUser();
        return ResponseEntity.ok(updateService.addUpdate(taskId, req, user));
    }

    @GetMapping("/{taskId}/updates")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskUpdateDTO>> getUpdates(@PathVariable Long taskId) {
        var user = currentUserProvider.requireUser();
        return ResponseEntity.ok(updateService.listUpdates(taskId, user));
    }
}
