package project.management.service;

import project.management.exception.ApplicationException;
import project.management.project_enum.ProjectType;
import project.management.dto.request.CommentRequest;

public interface CommentServiceInterface {
    void addComment(Long id, CommentRequest comment) throws ApplicationException;

    void deleteComment(Long id, ProjectType type);

    void updateComment(Long id, CommentRequest comment);
}
