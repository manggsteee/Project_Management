package project.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.management.dto.AttachmentDTO;
import project.management.dto.WorkDTO;
import project.management.exception.ResourceNotFoundException;
import project.management.model.Task;
import project.management.model.User;
import project.management.model.Work;
import project.management.model.attachment.WorkAttachment;
import project.management.project_enum.ProjectType;
import project.management.repository.TaskRepository;
import project.management.repository.WorkRepository;
import project.management.request.WorkRequest;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static project.management.project_enum.ProjectType.work_submit;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkService implements WorkInterface {
    private final WorkRepository workRepository;
    private final TaskRepository taskRepository;
    private final AttachmentService attachmentService;

    @Override
    public WorkDTO creatework(Long taskId, WorkRequest request, List<MultipartFile> files) {
        Work work = taskRepository.findById(taskId)
                .map(task ->
                        workRepository.save(Work.builder()
                                .task(task)
                                .title(request.getTitle())
                                .description(request.getDescription())
                                .startDate(request.getStartDate())
                                .endDate(request.getEndDate())
                                .priority(request.getPriority())
                                .build())
                )
                .orElseThrow(() -> {
                    log.error("The task does not exist");
                    return new RuntimeException("The task does not exist");
                });
        log.info("Work created with id {}", work.getId());
        User user = getMember(request.getMemberName(), work.getTask());
        if (user != null)
            work.setUser(user);
        work.setWorkAttachments(attachmentService.addAttachments(files, work.getId(), ProjectType.work, work));
        workRepository.save(work);
        log.info("Attachments had saved into Work with id {}", work.getId());
        return getWorkDTO(work);
    }

    @Override
    public WorkDTO updateWork(Long workId, WorkRequest request, List<MultipartFile> files) {
        log.info("updateWork");
        return getWorkDTO(
                workRepository.findById(workId).map(foundWork -> {
                            foundWork.setTitle(request.getTitle());
                            foundWork.setDescription(request.getDescription());
                            foundWork.setWorkStatus(request.getStatus());
                            foundWork.setStartDate(request.getStartDate());
                            foundWork.setEndDate(request.getEndDate());
                            foundWork.setPriority(request.getPriority());
                            addNewAttachments(files, foundWork, ProjectType.work);
                            User user = getMember(request.getMemberName(), foundWork.getTask());
                            if (user != null)
                                foundWork.setUser(user);
                            return workRepository.save(foundWork);
                        })
                        .orElseThrow(() -> new ResourceNotFoundException("Work not found"))
        );
    }

    private void addNewAttachments(List<MultipartFile> files, Work foundWork, ProjectType type) {
        List<WorkAttachment> newTaskAttachments = attachmentService.addAttachments(
                files, foundWork.getId(), type, foundWork);
        List<WorkAttachment> existingAttachments = foundWork.getWorkAttachments();
        existingAttachments.clear();
        existingAttachments.addAll(newTaskAttachments);
    }

    @Override
    public void deleteWork(Long workId) {
        workRepository.findById(workId).ifPresentOrElse(work -> {
                    workRepository.delete(work);
                    attachmentService.deleteFolder(Path.of(
                            attachmentService.getFolderDirection()
                                    + "Work\\" + workId));
                    attachmentService.deleteFolder(Path.of(
                            attachmentService.getFolderDirection()
                                    + "Work_Submit\\" + workId));

                },
                () -> {
                    throw new ResourceNotFoundException("Work not found");
                });
    }

    @Override
    public void submitWork(Long id,List<MultipartFile> files) {
        Work work = workRepository.getWorkById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work not found"));
        addNewAttachments(files, work, work_submit);
    }

    private WorkDTO getWorkDTO(Work work) {
        return WorkDTO.builder()
                .id(work.getId())
                .title(work.getTitle())
                .description(work.getDescription())
                .startDate(work.getStartDate())
                .endDate(work.getEndDate())
                .priority(work.getPriority())
                .workAttachments(work.getWorkAttachments().stream()
                        .map(workAttachment -> AttachmentDTO.builder()
                                .filename(workAttachment.getFilename())
                                .contentType(workAttachment.getContentType())
                                .build())
                        .toList())
                .member(Optional.ofNullable(work.getUser()).map(User::getUsername).orElse(""))
                .build();
    }

    private User getMember(String username, Task task) {
        return username != null ?
                task.getUsers().stream()
                        .filter(user -> user.getUsername().equals(username))
                        .findFirst()
                        .orElse(null)
                : null;
    }
}
