package project.management.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.management.model.comment.TaskComment;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
}
