package project.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.management.exception.ApplicationException;
import project.management.model.MemberRole;
import project.management.model.Project;
import project.management.model.User;
import project.management.model.comment.ProjectComment;
import project.management.model.comment.TaskComment;
import project.management.model.comment.WorkComment;
import project.management.project_enum.ExceptionEnum;
import project.management.project_enum.ProjectType;
import project.management.repository.ProjectRepository;
import project.management.repository.TaskRepository;
import project.management.repository.WorkRepository;
import project.management.repository.comment.ProjectCommentRepository;
import project.management.repository.comment.TaskCommentRepository;
import project.management.repository.comment.WorkCommentRepository;
import project.management.dto.request.CommentRequest;

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
    public void addComment(Long id, CommentRequest comment) throws ApplicationException{
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
                        throw new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND);
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
                        throw new ApplicationException(ExceptionEnum.TASK_NOT_FOUND);
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
                        throw new ApplicationException(ExceptionEnum.WORK_NOT_FOUND);
                    });
            default -> throw new ApplicationException(ExceptionEnum.WRONG_COMMENT_TYPE);
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
                                .orElseThrow(() -> new ApplicationException(ExceptionEnum.DENIED_USER_COMMENT_PROJECT)))
                .orElseThrow(() -> new ApplicationException(ExceptionEnum.USER_NOT_FOUND));
    }
}
