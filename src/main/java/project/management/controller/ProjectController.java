package project.management.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private String getUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping("create")
    public ApiResponse createProject(
            @RequestPart("project_information") ProjectRequest request,
            @RequestPart(value = "description_files", required = false) List<MultipartFile> files) {

        return ApiResponse.builder()
                .message("Create Project Successfully")
                .data(projectService.createProject(getUserName(),request, files))
                .build();
    }

    @PutMapping("{projectId}/update")
    public ApiResponse updateProject(
            @PathVariable Long projectId,
            @RequestPart("update_informations") ProjectRequest request,
            @RequestPart(value = "update_attachments", required = false) List<MultipartFile> files) {
        return ApiResponse.builder()
                .message("Update Project Successfully")
                .data(projectService.updateProject(getUserName(),projectId, request, files))
                .build();
    }

    @DeleteMapping("{projectId}/delete")
    public ApiResponse deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(getUserName(),projectId);
        return ApiResponse.builder()
                .message("Delete Project Successfully")
                .build();
    }

    @GetMapping("get/by/id")
    public ApiResponse getProjectById(
            @RequestParam("id") Long projectId) {
        return ApiResponse.builder()
                .message("Project with id " + projectId + " is found.")
                .data(projectService.getProjectById(getUserName(), projectId))
                .build();
    }

    @GetMapping("get/by/name")
    public ApiResponse getProjectsByName(
            @RequestParam("name") String projectName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.builder()
                .message("Projects with name " + projectName + " are found.")
                .data(projectService.getProjectsByName(getUserName(), projectName, page, size))
                .build();
    }

    @GetMapping("get/by/start_date")
    public ApiResponse getProjectsByStartDate(
            @RequestParam("start_date") LocalDateTime startDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.builder()
                .message("Projects with start date " + startDate + " are found.")
                .data(projectService.getProjectsByStartDate(getUserName(), startDate, page, size))
                .build();
    }

    @GetMapping("get/by/end_date")
    public ApiResponse getProjectsByEndDate(
            @RequestParam("end_date") LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.builder()
                .message("Projects with end date " + endDate + " are found.")
                .data(projectService.getProjectsByEndDate(getUserName(), endDate, page, size))
                .build();
    }

    @GetMapping("get/by/status")
    public ApiResponse getProjectsByStatus(
            @RequestParam("status") ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.builder()
                .message("Projects with status " + status + " are found.")
                .data(projectService.getProjectsByStatus(getUserName(), status, page, size))
                .build();
    }

    @GetMapping("get/by/created_date")
    public ApiResponse getProjectsByCreatedDate(
            @RequestParam("created_date") LocalDateTime createdDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.builder()
                .message("Projects with created date " + createdDate + " are found.")
                .data(projectService.getProjectsByCreatedDate(getUserName(), createdDate, page, size))
                .build();
    }

    @GetMapping("get/attachments")
    public ApiResponse getAttachments(@RequestParam("id") Long projectId) {
        return ApiResponse.builder()
                .message("Get Attachment Successfully")
                .data(projectService.getProjectAttachments(projectId))
                .build();
    }

    @GetMapping("attachment/download/")
    public ResponseEntity<byte[]> downloadAttachment(@RequestParam("id") Long attachmentId) throws IOException {
        ProjectAttachment attachment = projectService.getFilePath(attachmentId);
        File file = new File(attachment.getFilePath() + "\\" + attachment.getFilename());
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFilename() + "\"")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .body(fileContent);
    }
}