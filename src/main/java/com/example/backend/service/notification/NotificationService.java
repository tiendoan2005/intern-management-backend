package com.example.backend.service.notification;

import com.example.backend.entity.InternProfile;
import com.example.backend.entity.Notification;
import com.example.backend.entity.Task;
import com.example.backend.entity.TaskUpdate;
import com.example.backend.entity.User;
import com.example.backend.enums.NotificationType;
import com.example.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void notifyMentorTaskUpdated(Task task, InternProfile intern, TaskUpdate update) {
        // Resolve mentor user: Task -> ProgramGroup -> Mentor -> User
        User mentorUser = null;
        if (task.getGroup() != null && task.getGroup().getMentor() != null) {
            mentorUser = task.getGroup().getMentor().getUser();
        }

        // fallback: task.createdBy (nếu mentor là người tạo task)
        if (mentorUser == null) {
            mentorUser = task.getCreatedBy();
        }

        if (mentorUser == null) return;

        String internName = (intern.getUser() != null) ? intern.getUser().getFullName() : "Intern";
        Integer pct = update.getProgressPercent() != null ? update.getProgressPercent() : 0;

        Notification n = new Notification();
        n.setUser(mentorUser);
        n.setType(NotificationType.TASK);
        n.setTitle("Cập nhật tiến độ công việc");
        n.setContent(internName + " đã cập nhật tiến độ task #" + task.getId() + ": " + pct + "%");
        n.setRead(false);

        notificationRepository.save(n);
    }
}
