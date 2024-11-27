package project.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class ProjectService implements IProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final ProjectAttachmentRepository projectAttachmentRepository;

    @Override
    @CacheEvict(value = "projects", allEntries = true, condition = "#username != null")
    public ProjectDTO createProject(
            String username,
            ProjectRequest request,
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
    @CacheEvict(value = "projects", allEntries = true, condition = "#username != null")
    public ProjectDTO updateProject(String username,Long projectId, ProjectRequest request, List<MultipartFile> files) {
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
    @CacheEvict(value = "projects", allEntries = true, condition = "#username != null")
    public void deleteProject(String username,Long projectId) {
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
    @Cacheable(value = "projects", key = "#projectId")
    public ProjectDTO getProjectById(String userName, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND));
        if (!checkExistUser(userName, project)) {
            throw new ApplicationException(ExceptionEnum.DENIED_USER_FOUND_PROJECT);
        }
        return getProjectDTO(project);
    }

    @Override
    @Cacheable(value = "projects", key = "#userName + '_' + #projectName + '_' + #page + '_' + #size")
    public List<ProjectDTO> getProjectsByName(String userName, String projectName, int page, int size) {
        Page<Project> projectPage = projectRepository.findByName(projectName, PageRequest.of(page, size));
        List<Project> projects = getProjectsIfUserExists(userName, projectPage.getContent());

        if (projects.isEmpty()) {
            throw new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND);
        }
        return getProjectDTOs(projects);
    }

    @Override
    @Cacheable(value = "projects", key = "#userName + '_' + #startDate + '_' + #page + '_' + #size")
    public List<ProjectDTO> getProjectsByStartDate(String userName, LocalDateTime startDate, int page, int size) {
        Page<Project> projectPage = projectRepository.findByStartDate(startDate, PageRequest.of(page, size));
        List<Project> projects = getProjectsIfUserExists(userName, projectPage.getContent());

        if (projects.isEmpty()) {
            throw new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND);
        }
        return getProjectDTOs(projects);
    }

    @Override
    @Cacheable(value = "projects", key = "#userName + '_' + #endDate + '_' + #page + '_' + #size")
    public List<ProjectDTO> getProjectsByEndDate(String userName, LocalDateTime endDate, int page, int size) {
        Page<Project> projectPage = projectRepository.findByEndDate(endDate, PageRequest.of(page, size));
        List<Project> projects = getProjectsIfUserExists(userName, projectPage.getContent());

        if (projects.isEmpty()) {
            throw new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND);
        }
        return getProjectDTOs(projects);
    }

    @Override
    @Cacheable(value = "projects", key = "#userName + '_' + #status + '_' + #page + '_' + #size")
    public List<ProjectDTO> getProjectsByStatus(String userName, ProjectStatus status, int page, int size) {
        Page<Project> projectPage = projectRepository.findByStatus(status, PageRequest.of(page, size));
        List<Project> projects = getProjectsIfUserExists(userName, projectPage.getContent());

        if (projects.isEmpty()) {
            throw new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND);
        }
        return getProjectDTOs(projects);
    }

    @Override
    @Cacheable(value = "projects", key = "#userName + '_' + #createdDate + '_' + #page + '_' + #size")
    public List<ProjectDTO> getProjectsByCreatedDate(String userName, LocalDateTime createdDate, int page, int size) {
        Page<Project> projectPage = projectRepository.findByCreatedAt(createdDate, PageRequest.of(page, size));
        List<Project> projects = getProjectsIfUserExists(userName, projectPage.getContent());

        if (projects.isEmpty()) {
            throw new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND);
        }
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
