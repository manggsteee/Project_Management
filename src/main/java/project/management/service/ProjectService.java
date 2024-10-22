package project.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.management.dto.AttachmentDTO;
import project.management.dto.MemberRoleDTO;
import project.management.dto.ProjectDTO;
import project.management.exception.DeniedUserException;
import project.management.exception.ResourceNotFoundException;
import project.management.model.MemberRole;
import project.management.model.Project;
import project.management.model.attachment.ProjectAttachment;
import project.management.project_enum.ProjectStatus;
import project.management.project_enum.ProjectType;
import project.management.repository.ProjectRepository;
import project.management.repository.UserRepository;
import project.management.repository.attachment.ProjectAttachmentRepository;
import project.management.request.MemberRolesRequest;
import project.management.request.ProjectRequest;

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
        }).orElseThrow(() -> new ResourceNotFoundException("Project not found"));

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
                            throw new ResourceNotFoundException("Project not found");
                        });
    }

    @Override
    public ProjectDTO getProjectById(String userName, Long projectId) {
        Project project = projectRepository.findById(projectId).
                orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!checkExistUser(userName, project))
            throw new DeniedUserException(
                    "Project with id " + projectId +
                            " exists but don't have any User with username: "
                            + userName);
        return getProjectDTO(project);
    }

    @Override
    public List<ProjectDTO> getProjectsByName(String userName, String projectName) {
        List<Project> projects = getProjectsIfUserExists(userName,
                projectRepository.getProjectByName(projectName)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Can't found any project with name: " +
                                        projectName)));
        if (projects.isEmpty())
            throw new DeniedUserException(
                    "User with username: " + userName +
                            " doesn't in any Project with name: " + projectName
            );
        return getProjectDTOs(projects);
    }

    @Override
    public List<ProjectDTO> getProjectsByStartDate(String userName, LocalDateTime startDate) {
        List<Project> projects = getProjectsIfUserExists(userName,
                projectRepository.getProjectByStartDate(startDate)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Can't found any project with Start Date: " +
                                        startDate)));
        if (projects.isEmpty())
            throw new DeniedUserException(
                    "User with username: " + userName +
                            " doesn't in any Project with Start Date: "
                            + startDate
            );
        return getProjectDTOs(projects);
    }

    @Override
    public List<ProjectDTO> getProjectsByEndDate(String userName, LocalDateTime endDate) {
        List<Project> projects = getProjectsIfUserExists(userName,
                projectRepository.getProjectByEndDate(endDate)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Can't found any project with endDate: "
                                        + endDate)));
        if (projects.isEmpty())
            throw new DeniedUserException(
                    "User with username: " + userName +
                            " doesn't in any Project with End Date: "
                            + endDate
            );
        return getProjectDTOs(projects);
    }

    @Override
    public List<ProjectDTO> getProjectsByStatus(String userName, ProjectStatus status) {
        List<Project> projects = getProjectsIfUserExists(userName,
                projectRepository.getProjectByStatus(status)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Can't found any project with status: " + status)));
        if (projects.isEmpty())
            throw new DeniedUserException(
                    "User with username: " + userName +
                            " doesn't in any Project with Status: "
                            + status
            );
        return getProjectDTOs(projects);
    }

    @Override
    public List<ProjectDTO> getProjectsByCreatedDate(String userName, LocalDateTime createdDate) {
        List<Project> projects = getProjectsIfUserExists(userName,
                projectRepository.getProjectByCreatedAt(createdDate)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Can't found any project with created Date: "
                                        + createdDate)));
        if (projects.isEmpty())
            throw new DeniedUserException(
                    "User with username: " + userName +
                            " doesn't in any Project with Created Date: "
                            + createdDate
            );
        return getProjectDTOs(projects);
    }

    @Override
    public List<String> getProjectAttachments(Long projectId) {
        return projectRepository.findById(projectId)
                .map(project -> project.getProjectAttachments().stream()
                        .map(projectAttachment ->
                                "http://localhost:8080/project_management/v1/project/attachment/download/" + projectAttachment.getId()
                                ).toList())
                .orElseThrow(() -> new ResourceNotFoundException("Files not found"));
    }

    @Override
    public ProjectAttachment getFilePath(Long attachmentId) {
        return projectAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));
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
