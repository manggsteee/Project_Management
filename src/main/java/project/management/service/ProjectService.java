package project.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.management.dto.response.AttachmentDTO;
import project.management.dto.response.MemberRoleDTO;
import project.management.dto.response.ProjectDTO;
import project.management.exception.ApplicationException;
import project.management.model.MemberRole;
import project.management.model.Project;
import project.management.model.attachment.ProjectAttachment;
import project.management.project_enum.ExceptionEnum;
import project.management.project_enum.ProjectStatus;
import project.management.project_enum.ProjectType;
import project.management.repository.ProjectRepository;
import project.management.repository.UserRepository;
import project.management.repository.attachment.ProjectAttachmentRepository;
import project.management.dto.request.MemberRolesRequest;
import project.management.dto.request.ProjectRequest;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService implements ProjectServiceInterface {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final ProjectAttachmentRepository projectAttachmentRepository;

    @Override
    public ProjectDTO createProject(ProjectRequest request,
                                    List<MultipartFile> files) {
        log.info("Create new project: {}", request.getTitle());
        Project project = projectRepository.save(
                Project.builder()
                        .name(request.getTitle())
                        .description(request.getDescription())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .build()
        );
        log.info("Project created with ID: {}", project.getId());
        project.setProjectAttachments(attachmentService
                .addAttachments(files, project.getId(), ProjectType.project, project));
        project.setMemberRoles(getMemberRoles(project.getId(), request.getMemberRoles()));
        projectRepository.save(project);
        return getProjectDTO(project);
    }

    @Override
    public ProjectDTO updateProject(Long projectId, ProjectRequest request, List<MultipartFile> files) {
        Project project = projectRepository.findById(projectId).map(foundProject -> {
            foundProject.setName(request.getTitle());
            foundProject.setDescription(request.getDescription());
            foundProject.setStartDate(request.getStartDate());
            foundProject.setEndDate(request.getEndDate());
            foundProject.setStatus(request.getStatus());
            List<ProjectAttachment> newAttachments = attachmentService.addAttachments(
                    files, foundProject.getId(), ProjectType.project, foundProject);
            List<ProjectAttachment> existingAttachments = foundProject.getProjectAttachments();
            existingAttachments.clear();
            existingAttachments.addAll(newAttachments);
            List<MemberRole> newRoles = getMemberRoles(projectId, request.getMemberRoles());
            List<MemberRole> existingRoles = foundProject.getMemberRoles();
            existingRoles.clear();
            existingRoles.addAll(newRoles);
            return projectRepository.save(foundProject);
        }).orElseThrow(() -> new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND));

        return getProjectDTO(project);
    }

    @Override
    public void deleteProject(Long projectId) {
        List<Long> taskId = new ArrayList<>();
        List<Long> workId = new ArrayList<>();
        projectRepository.findById(projectId)
                .ifPresentOrElse(project -> {
                            Optional.ofNullable(project.getTasks()).ifPresent(
                                    tasks -> {
                                        tasks.forEach(task -> {
                                            Optional.ofNullable(task.getUsers())
                                                    .ifPresent(users -> {
                                                        users.forEach(user ->
                                                                user.getTasks().remove(task));
                                                        task.getUsers().clear();
                                                    });
                                            taskId.add(task.getId());
                                            Optional.ofNullable(task.getWorks())
                                                    .ifPresent(works -> works.forEach(
                                                            work -> workId.add(work.getId())));
                                        });
                                        project.getTasks().clear();
                                    });
                            projectRepository.delete(project);
                            attachmentService.deleteFolder(Path.of(
                                    attachmentService.getFolderDirection() + "Project\\" + projectId));
                            taskId.forEach(id -> attachmentService.deleteFolder(Path.of(
                                    attachmentService.getFolderDirection()
                                            + "Task\\" + id)));
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
                            throw new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND);
                        });
    }

    @Override
    public ProjectDTO getProjectById(String userName, Long projectId) {
        Project project = projectRepository.findById(projectId).
                orElseThrow(() -> new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND));
        if (!checkExistUser(userName, project))
            throw new ApplicationException(ExceptionEnum.DENIED_USER_FOUND_PROJECT);
        return getProjectDTO(project);
    }

    @Override
    public List<ProjectDTO> getProjectsByName(String userName, String projectName) {
        List<Project> projects = getProjectsIfUserExists(userName,
                projectRepository.getProjectByName(projectName)
                        .orElseThrow(() -> new ApplicationException(
                                ExceptionEnum.PROJECT_NOT_FOUND)));
        if (projects.isEmpty())
            throw new ApplicationException(
                    ExceptionEnum.PROJECT_NOT_FOUND);
        return getProjectDTOs(projects);
    }

    @Override
    public List<ProjectDTO> getProjectsByStartDate(String userName, LocalDateTime startDate) {
        List<Project> projects = getProjectsIfUserExists(userName,
                projectRepository.getProjectByStartDate(startDate)
                        .orElseThrow(() -> new ApplicationException(
                                ExceptionEnum.PROJECT_NOT_FOUND)));
        if (projects.isEmpty())
            throw new ApplicationException(
                    ExceptionEnum.PROJECT_NOT_FOUND);
        return getProjectDTOs(projects);
    }

    @Override
    public List<ProjectDTO> getProjectsByEndDate(String userName, LocalDateTime endDate) {
        List<Project> projects = getProjectsIfUserExists(userName,
                projectRepository.getProjectByEndDate(endDate)
                        .orElseThrow(() -> new ApplicationException(
                                ExceptionEnum.PROJECT_NOT_FOUND)));
        if (projects.isEmpty())
            throw new ApplicationException(
                    ExceptionEnum.PROJECT_NOT_FOUND);
        return getProjectDTOs(projects);
    }

    @Override
    public List<ProjectDTO> getProjectsByStatus(String userName, ProjectStatus status) {
        List<Project> projects = getProjectsIfUserExists(userName,
                projectRepository.getProjectByStatus(status)
                        .orElseThrow(() -> new ApplicationException(
                                ExceptionEnum.PROJECT_NOT_FOUND)));
        if (projects.isEmpty())
            throw new ApplicationException(
                    ExceptionEnum.PROJECT_NOT_FOUND);
        return getProjectDTOs(projects);
    }

    @Override
    public List<ProjectDTO> getProjectsByCreatedDate(String userName, LocalDateTime createdDate) {
        List<Project> projects = getProjectsIfUserExists(userName,
                projectRepository.getProjectByCreatedAt(createdDate)
                        .orElseThrow(() -> new ApplicationException(
                                ExceptionEnum.PROJECT_NOT_FOUND)));
        if (projects.isEmpty())
            throw new ApplicationException(
                    ExceptionEnum.PROJECT_NOT_FOUND);
        return getProjectDTOs(projects);
    }

    @Override
    public List<String> getProjectAttachments(Long projectId) {
        return projectRepository.findById(projectId)
                .map(project -> project.getProjectAttachments().stream()
                        .map(projectAttachment ->
                                "http://localhost:8080/project_management/v1/project/attachment/download/" + projectAttachment.getId()
                                ).toList())
                .orElseThrow(() -> new ApplicationException(ExceptionEnum.FILE_NOT_FOUND));
    }

    @Override
    public ProjectAttachment getFilePath(Long attachmentId) {
        return projectAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ApplicationException(ExceptionEnum.FILE_NOT_FOUND));
    }

    private List<MemberRole> getMemberRoles(Long projectId, List<MemberRolesRequest> request) {
        return request != null ?
                request.stream()
                        .map(member ->
                                userRepository.findByUsername(member.getUserName())
                                        .map(user -> {
                                            MemberRole memberRole = MemberRole.builder()
                                                    .user(user)
                                                    .project(Project.builder()
                                                            .id(projectId)
                                                            .build())
                                                    .role(member.getRole())
                                                    .build();
                                            log.info("Member with username {} is added successfully", member.getUserName());
                                            return memberRole;
                                        })
                                        .orElseGet(() -> {
                                            log.warn("Member with username {} not found", member.getUserName());
                                            return null;
                                        }))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                : new ArrayList<>();
    }

    private List<ProjectDTO> getProjectDTOs(List<Project> projects) {
        return projects.stream().map(this::getProjectDTO).collect(Collectors.toList());
    }

    private ProjectDTO getProjectDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus())
                .projectAttachments(
                        project.getProjectAttachments().stream()
                                .map(projectAttachment ->
                                        AttachmentDTO.builder()
                                                .filename(projectAttachment.getFilename())
                                                .contentType(projectAttachment.getContentType())
                                                .build()
                                )
                                .toList()
                )
                .memberRoles(
                        Optional.ofNullable(project.getMemberRoles()).orElseGet(ArrayList::new)
                                .stream()
                                .map(member ->
                                        MemberRoleDTO.builder()
                                                .userName(member.getUser().getUsername())
                                                .role(member.getRole())
                                                .build())
                                .toList()
                )
                .build();
    }

    private boolean checkExistUser(String username, Project project) {
        return Optional.ofNullable(project.getMemberRoles()).map(
                        memberRoles -> memberRoles.stream().anyMatch(
                                memberRole -> memberRole.getUser().getUsername().equals(username)))
                .orElse(false);
    }

    private List<Project> getProjectsIfUserExists(String userName, List<Project> projects) {
        return projects.stream().filter(project -> checkExistUser(userName, project)).toList();
    }
}
