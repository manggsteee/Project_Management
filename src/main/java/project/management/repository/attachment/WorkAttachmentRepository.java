package project.management.repository.attachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.management.model.attachment.WorkAttachment;

@Repository
public interface WorkAttachmentRepository extends JpaRepository<WorkAttachment, Long> {
}
