package project.management.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.management.model.attachment.ProjectAttachment;
import project.management.project_enum.ProjectStatus;
import project.management.dto.request.ProjectRequest;
import project.management.response.ApiResponse;
import project.management.service.ProjectService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/project")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("create")
    public ApiResponse createProject(
            @RequestPart("project_information") ProjectRequest request,
            @RequestPart(value = "description_files",
                    required = false) List<MultipartFile> files) {
            return ApiResponse.builder()
                    .message("Create Project Successfully")
                    .data(projectService.createProject(request, files))
                    .build();
    }

    @PutMapping("{projectId}/update")
    public ApiResponse updateProject(
            @PathVariable Long projectId,
            @RequestPart("update_informations") ProjectRequest request,
            @RequestPart(value = "update_attachments",
                    required = false) List<MultipartFile> files
    ) {
            return ApiResponse.builder()
                    .message("Update Project Successfully")
                    .data(projectService.updateProject(projectId, request, files))
                    .build();
    }

    @DeleteMapping("{projectId}/delete")
    public ApiResponse deleteProject(
            @PathVariable Long projectId
    ) {
            projectService.deleteProject(projectId);
            return ApiResponse.builder()
                    .message("Delete Project Successfully")
                    .build();
    }

    @GetMapping("get/by/id")
    public ApiResponse getProjectById(
            @RequestParam("username") String userName,
            @RequestParam("id") Long projectId) {
            return ApiResponse .builder()
                    .message("Project with id "
                    + projectId + " is found.")
                    .data(projectService.getProjectById(
                            userName,projectId))
                    .build();
    }

    @GetMapping("get/by/name")
    public ApiResponse getProjectByName(
            @RequestParam("username") String userName,
            @RequestParam("name") String projectName) {

            return ApiResponse.builder()
                    .message("Projects with name "
                    + projectName + " are found.")
                    .data(projectService.getProjectsByName(
                            userName, projectName))
                    .build();
    }

    @GetMapping("get/by/start_date")
    public ApiResponse getProjectByStartDate(
            @RequestParam("username") String userName,
            @RequestParam("start_date") LocalDateTime startDate) {
            return ApiResponse.builder()
                    .message("Project with start date "
                    + startDate + " are found.")
                    .data(projectService.getProjectsByStartDate(
                            userName, startDate))
                    .build();
    }

    @GetMapping("get/by/end_date")
    public ApiResponse getProjectByEndDate(
            @RequestParam("username") String userName,
            @RequestParam("end_date") LocalDateTime endDate) {
            return ApiResponse.builder()
                    .message("Project with end date "
                    + endDate + " are found.")
                    .data(projectService.getProjectsByEndDate(
                            userName, endDate))
                    .build();
    }

    @GetMapping("get/by/status")
    public ApiResponse getProjectByStatus(
            @RequestParam("username") String userName,
            @RequestParam("status") ProjectStatus status) {
            return ApiResponse
                    .builder()
                    .message("Project with status "
                    + status + " are found.")
                    .data(projectService.getProjectsByStatus(
                            userName, status))
                    .build();
    }

    @GetMapping("get/by/created_date")
    public ApiResponse getProjectByCreatedDate(
            @RequestParam("username") String userName,
            @RequestParam("created_date") LocalDateTime createdDate) {
            return ApiResponse
                    .builder()
                    .message("Project with created date "
                    + createdDate + " are found.")
                    .data(projectService.getProjectsByCreatedDate(
                            userName, createdDate))
                    .build();
    }

    @GetMapping("get/attachments")
    public ApiResponse getAttachments(
            @RequestParam("id") Long projectId
    ) {
        return ApiResponse.builder()
                .message("Get Attachment Successfully")
                .data(projectService.getProjectAttachments(projectId))
                .build();
    }

    @GetMapping("attachment/download/")
    public ResponseEntity<byte[]> downloadAttachment(
            @RequestParam("id") Long attachmentId
    ) throws IOException {
            ProjectAttachment attachment = projectService.getFilePath(attachmentId);
            File file = new File(attachment.getFilePath() + "\\" + attachment.getFilename());
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + attachment.getFilename() + "\"")
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .body(fileContent);
    }
}
