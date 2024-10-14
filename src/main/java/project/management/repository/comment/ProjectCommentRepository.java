package project.management.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.management.model.comment.ProjectComment;

@Repository
public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long> {
}
