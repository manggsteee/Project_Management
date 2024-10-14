package project.management.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.management.model.comment.WorkComment;

@Repository
public interface WorkCommentRepository extends JpaRepository<WorkComment, Long> {
}
