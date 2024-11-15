package project.management.service;

import org.springframework.web.multipart.MultipartFile;
import project.management.dto.response.ProjectDTO;
import project.management.model.attachment.ProjectAttachment;
import project.management.project_enum.ProjectStatus;
import project.management.dto.request.ProjectRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface IProjectService {
    ProjectDTO createProject(String username,ProjectRequest request,
                             List<MultipartFile> files);

    ProjectDTO updateProject(String username,Long projectId,
                             ProjectRequest request,
                             List<MultipartFile> files);

    void deleteProject(String username,Long projectId);

    ProjectDTO getProjectById(String userName, Long projectId);

    List<ProjectDTO> getProjectsByName(String userName, String projectName, int page, int size);

    List<ProjectDTO> getProjectsByStartDate(String userName, LocalDateTime startDate, int page, int size);

    List<ProjectDTO> getProjectsByEndDate(String userName, LocalDateTime endDate, int page, int size);

    List<ProjectDTO> getProjectsByStatus(String userName, ProjectStatus status, int page, int size);

    List<ProjectDTO> getProjectsByCreatedDate(String userName, LocalDateTime createdDate, int page, int size);

    List<String> getProjectAttachments(Long projectId);

    ProjectAttachment getFilePath(Long attachmentId);
}