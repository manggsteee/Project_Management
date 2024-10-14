package project.management.service;

import org.springframework.web.multipart.MultipartFile;
import project.management.dto.ProjectDTO;
import project.management.request.ProjectRequest;

import java.util.List;

public interface ProjectServiceInterface {
    ProjectDTO createProject(ProjectRequest request,
                             List<MultipartFile> files);

    ProjectDTO updateProject(
            Long projectId,
            ProjectRequest request,
            List<MultipartFile> files);

    void deleteProject(Long projectId);
}
