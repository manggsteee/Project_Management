package project.management.repository.attachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.management.model.attachment.ProjectAttachment;

@Repository
public interface ProjectAttachmentRepository extends JpaRepository<ProjectAttachment, Long> {
}
