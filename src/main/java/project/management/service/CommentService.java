package project.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.management.exception.DeniedUserException;
import project.management.exception.ResourceNotFoundException;
import project.management.exception.WrongTypeException;
import project.management.model.MemberRole;
import project.management.model.Project;
import project.management.model.User;
import project.management.model.comment.ProjectComment;
import project.management.model.comment.TaskComment;
import project.management.model.comment.WorkComment;
import project.management.project_enum.ProjectType;
import project.management.repository.ProjectRepository;
import project.management.repository.TaskRepository;
import project.management.repository.WorkRepository;
import project.management.repository.comment.ProjectCommentRepository;
import project.management.repository.comment.TaskCommentRepository;
import project.management.repository.comment.WorkCommentRepository;
import project.management.request.CommentRequest;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentServiceInterface {
    private final ProjectCommentRepository projectCommentRepository;
    private final ProjectRepository projectRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final TaskRepository taskRepository;
    private final WorkCommentRepository workCommentRepository;
    private final WorkRepository workRepository;

    @Override
    public void addComment(Long id, CommentRequest comment) throws WrongTypeException{
        switch (comment.getType()) {
            case project -> projectRepository.findById(id).ifPresentOrElse(project -> {
                        ProjectComment projectComment = ProjectComment.builder()
                                .user(checkSender(project, comment.getSender()))
                                .comment(comment.getComment())
                                .project(project)
                                .build();
                        projectCommentRepository.save(projectComment);
                    },
                    () -> {
                        throw new ResourceNotFoundException("Project not found");
                    }
            );
            case task -> taskRepository.findById(id).ifPresentOrElse(task -> {
                        TaskComment taskComment = TaskComment.builder()
                                .user(checkSender(task.getProject(), comment.getSender()))
                                .comment(comment.getComment())
                                .task(task)
                                .build();
                        taskCommentRepository.save(taskComment);
                    },
                    () -> {
                        throw new ResourceNotFoundException("Task not found");
                    });
            case work -> workRepository.findById(id).ifPresentOrElse(work -> {
                        WorkComment workComment = WorkComment.builder()
                                .user(checkSender(work.getTask().getProject(), comment.getSender()))
                                .comment(comment.getComment())
                                .work(work)
                                .build();
                        workCommentRepository.save(workComment);
                    },
                    () -> {
                        throw new ResourceNotFoundException("Work not found");
                    });
            default -> throw new WrongTypeException("Wrong comment type");
        }

    }

    @Override
    public void deleteComment(Long id, ProjectType type) {

    }

    @Override
    public void updateComment(Long id, CommentRequest comment) {

    }

    private User checkSender(Project project, String sender) {
        return Optional.ofNullable(project.getMemberRoles()).map(memberRoles ->
                        memberRoles.stream().filter(memberRole ->
                                        memberRole.getUser().getUsername().equals(sender)
                                ).findFirst().map(MemberRole::getUser)
                                .orElseThrow(() -> new DeniedUserException("User with username: "
                                        + sender + " doesn't have permission to comment to this project")))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project doesn't have any member yet"));
    }
}
