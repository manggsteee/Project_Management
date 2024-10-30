package project.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.management.dto.response.AttachmentDTO;
import project.management.dto.response.TaskDTO;
import project.management.exception.ApplicationException;
import project.management.model.Project;
import project.management.model.Task;
import project.management.model.User;
import project.management.model.attachment.TaskAttachment;
import project.management.project_enum.ExceptionEnum;
import project.management.project_enum.ProjectType;
import project.management.repository.ProjectRepository;
import project.management.repository.TaskRepository;
import project.management.repository.UserRepository;
import project.management.dto.request.TaskRequest;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService implements TaskServiceInterface {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;

    @Override
    public TaskDTO createTask(
            Long projectID,
            TaskRequest request,
            List<MultipartFile> files) {
        log.info("createTask");
        Task task = projectRepository.findById(projectID).map(project ->
                taskRepository.save(Task.builder()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .project(project)
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .users(getMembers(request.getUserNames(), project))
                        .priority(request.getPriority())
                        .build())
        ).orElseThrow(() -> {
                    log.error("Project with id {} can't be found", projectID);
                    return new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND);
                }
        );
        log.info("Task created with id {}", task.getId());
        task.setTaskAttachments(attachmentService.addAttachments(files, task.getId(), ProjectType.task, task));
        taskRepository.save(task);
        log.info("Attachments had saved into Task with id {}", task.getId());
        return getTaskDTO(task, getNotAddedMembers(request.getUserNames(), task));
    }

    @Override
    public TaskDTO updateTask(Long taskId, TaskRequest updateTaskRequest, List<MultipartFile> files) {
        log.info("updateTask");
        Task task = taskRepository.findById(taskId).map(foundTask -> {
                    foundTask.setTitle(updateTaskRequest.getTitle());
                    foundTask.setDescription(updateTaskRequest.getDescription());
                    foundTask.setTaskStatus(updateTaskRequest.getStatus());
                    foundTask.setStartDate(updateTaskRequest.getStartDate());
                    foundTask.setEndDate(updateTaskRequest.getEndDate());
                    foundTask.setPriority(updateTaskRequest.getPriority());
                    List<TaskAttachment> newTaskAttachments = attachmentService.addAttachments(
                            files, foundTask.getId(), ProjectType.task, foundTask);
                    List<TaskAttachment> existingAttachments = foundTask.getTaskAttachments();
                    existingAttachments.clear();
                    existingAttachments.addAll(newTaskAttachments);
                    List<User> newMembers = getMembers(
                            updateTaskRequest.getUserNames(), foundTask.getProject());
                    List<User> existingMembers = foundTask.getUsers();
                    existingMembers.clear();
                    existingMembers.addAll(newMembers);
                    return taskRepository.save(foundTask);
                })
                .orElseThrow(() -> new ApplicationException(ExceptionEnum.TASK_NOT_FOUND));
        return getTaskDTO(task, getNotAddedMembers(updateTaskRequest.getUserNames(), task));
    }

    @Override
    public void deleteTask(Long taskId) {
        List<Long> workId = new ArrayList<>();
        taskRepository.findById(taskId).ifPresentOrElse(task -> {
                    Optional.ofNullable(task.getUsers())
                            .ifPresent(users -> {
                                users.forEach(user ->
                                        user.getTasks().remove(task));
                                task.getUsers().clear();
                            });
                    Optional.ofNullable(task.getWorks())
                            .ifPresent(works -> works.forEach(
                                    work -> workId.add(work.getId())));
                    taskRepository.delete(task);
                    attachmentService.deleteFolder(Path.of(attachmentService
                            .getFolderDirection() + "Task\\" + task.getId()));
                    workId.forEach(id -> {
                        attachmentService.deleteFolder(Path.of(
                                attachmentService.getFolderDirection()
                                        + "Work\\" + id));
                        attachmentService.deleteFolder(Path.of(
                                attachmentService.getFolderDirection()
                                        + "Work_Submit\\" + id));
                    });
                },
                () -> {
                    throw new ApplicationException(ExceptionEnum.TASK_NOT_FOUND);
                });
    }

    private List<User> getMembers(List<String> memberUserNames, Project project) {
        //Check memberUserNames from the request
        //If notnull get the userName from the list
        //check that if any member is added to the project
        //if notnull, check if any userName from the request
        //is equal to the userName is taken from the project
        //Add the User Object to the task
        return Optional.ofNullable(memberUserNames).orElseGet(ArrayList::new).stream()
                .filter(userName -> !project.getMemberRoles().isEmpty() &&
                        project.getMemberRoles().stream()
                                .anyMatch(memberRole -> memberRole.getUser()
                                        .getUsername().equals(userName)))
                .map(userRepository::findByUsername)
                .map(user -> user.orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<String> getNotAddedMembers(List<String> memberUserNames, Task task) {
        return Objects.requireNonNull(memberUserNames).stream()
                .filter(userName -> task.getUsers().stream()
                        .noneMatch(user -> user.getUsername().equals(userName))).toList();
    }

    private TaskDTO getTaskDTO(Task task, List<String> notAddedMembers) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .priority(task.getPriority())
                .taskAttachments(task.getTaskAttachments().stream()
                        .map(taskAttachment ->
                                AttachmentDTO.builder()
                                        .filename(taskAttachment.getFilename())
                                        .contentType(taskAttachment.getContentType())
                                        .build())
                        .toList())
                .members(task.getUsers().stream()
                        .map(User::getUsername)
                        .toList())
                .noneAddedMembers(notAddedMembers)
                .build();
    }
}
