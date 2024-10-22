package project.management.service;

import project.management.exception.WrongTypeException;
import project.management.project_enum.ProjectType;
import project.management.request.CommentRequest;

public interface CommentServiceInterface {
    void addComment(Long id, CommentRequest comment) throws WrongTypeException;

    void deleteComment(Long id, ProjectType type);

    void updateComment(Long id, CommentRequest comment);
}
