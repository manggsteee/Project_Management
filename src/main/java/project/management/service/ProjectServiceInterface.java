package project.management.service;

import org.springframework.web.multipart.MultipartFile;
import project.management.dto.response.ProjectDTO;
import project.management.model.attachment.ProjectAttachment;
import project.management.project_enum.ProjectStatus;
import project.management.dto.request.ProjectRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectServiceInterface {
    ProjectDTO createProject(ProjectRequest request,
                             List<MultipartFile> files);

    ProjectDTO updateProject(
            Long projectId,
            ProjectRequest request,
            List<MultipartFile> files);

    void deleteProject(Long projectId);

    ProjectDTO getProjectById(String userName, Long projectId);

    List<ProjectDTO> getProjectsByName(String userName, String projectName);

    List<ProjectDTO> getProjectsByStartDate(String userName, LocalDateTime startDate);

    List<ProjectDTO> getProjectsByEndDate(String userName, LocalDateTime endDate);

    List<ProjectDTO> getProjectsByStatus(String userName, ProjectStatus status);

    List<ProjectDTO> getProjectsByCreatedDate(String userName, LocalDateTime createdDate);

    List<String> getProjectAttachments(Long projectId);

    ProjectAttachment getFilePath(Long attachmentId);
}
