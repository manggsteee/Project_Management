package project.management.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.management.model.comment.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
