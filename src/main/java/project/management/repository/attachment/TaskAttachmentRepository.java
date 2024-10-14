package project.management.repository.attachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.management.model.attachment.TaskAttachment;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
}
