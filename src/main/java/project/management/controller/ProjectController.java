package project.management.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.management.exception.DeniedUserException;
import project.management.exception.ResourceNotFoundException;
import project.management.model.attachment.ProjectAttachment;
import project.management.project_enum.ProjectStatus;
import project.management.request.ProjectRequest;
import project.management.response.ApiResponse;
import project.management.service.ProjectService;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/project")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("create")
    public ResponseEntity<ApiResponse> createProject(
            @RequestPart("project_information") ProjectRequest request,
            @RequestPart(value = "description_files",
                    required = false) List<MultipartFile> files) {
        try {
            return ResponseEntity.ok(new ApiResponse("Create Project Successfully",
                    projectService.createProject(request, files)));
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Some exceptions have been found", null));
        }
    }

    @PutMapping("{projectId}/update")
    public ResponseEntity<ApiResponse> updateProject(
            @PathVariable Long projectId,
            @RequestPart("update_informations") ProjectRequest request,
            @RequestPart(value = "update_attachments",
                    required = false) List<MultipartFile> files
    ) {
        try {
            return ResponseEntity.ok(new ApiResponse("Update Project Successfully",
                    projectService.updateProject(projectId, request, files)));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found", null));
        }
    }

    @DeleteMapping("{projectId}/delete")
    public ResponseEntity<ApiResponse> deleteProject(
            @PathVariable Long projectId
    ) {
        try {
            projectService.deleteProject(projectId);
            return ResponseEntity.ok(new ApiResponse("Delete Project Successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found", null));
        }
    }

    @GetMapping("get/by/id")
    public ResponseEntity<ApiResponse> getProjectById(
            @RequestParam("username") String userName,
            @RequestParam("id") Long projectId) {
        try {
            return ResponseEntity.ok(new ApiResponse("Project with id "
                    + projectId + " is found.", projectService.getProjectById(userName,projectId)));
        } catch (ResourceNotFoundException e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (DeniedUserException e){
            log.error(String.valueOf(e));
            return ResponseEntity.status(FORBIDDEN).body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found", null));
        }
    }

    @GetMapping("get/by/name")
    public ResponseEntity<ApiResponse> getProjectByName(
            @RequestParam("username") String userName,
            @RequestParam("name") String projectName) {
        try {
            return ResponseEntity.ok(new ApiResponse("Project with name "
                    + projectName + " is found.", projectService.getProjectsByName(userName, projectName)));
        } catch (ResourceNotFoundException e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (DeniedUserException e){
            log.error(String.valueOf(e));
            return ResponseEntity.status(FORBIDDEN).body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found", null));
        }
    }

    @GetMapping("get/by/start_date")
    public ResponseEntity<ApiResponse> getProjectByStartDate(
            @RequestParam("username") String userName,
            @RequestParam("start_date") LocalDateTime startDate) {
        try {
            return ResponseEntity.ok(new ApiResponse("Project with start date "
                    + startDate + " is found.", projectService.getProjectsByStartDate(userName, startDate)));
        } catch (ResourceNotFoundException e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (DeniedUserException e){
            log.error(String.valueOf(e));
            return ResponseEntity.status(FORBIDDEN).body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found", null));
        }
    }

    @GetMapping("get/by/end_date")
    public ResponseEntity<ApiResponse> getProjectByEndDate(
            @RequestParam("username") String userName,
            @RequestParam("end_date") LocalDateTime endDate) {
        try {
            return ResponseEntity.ok(new ApiResponse("Project with end date "
                    + endDate + " is found.", projectService.getProjectsByEndDate(userName, endDate)));
        } catch (ResourceNotFoundException e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (DeniedUserException e){
            log.error(String.valueOf(e));
            return ResponseEntity.status(FORBIDDEN).body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found", null));
        }
    }

    @GetMapping("get/by/status")
    public ResponseEntity<ApiResponse> getProjectByStatus(
            @RequestParam("username") String userName,
            @RequestParam("status") ProjectStatus status) {
        try {
            return ResponseEntity.ok(new ApiResponse("Project with status "
                    + status + " is found.", projectService.getProjectsByStatus(userName, status)));
        } catch (ResourceNotFoundException e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (DeniedUserException e){
            log.error(String.valueOf(e));
            return ResponseEntity.status(FORBIDDEN).body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found", null));
        }
    }

    @GetMapping("get/by/created_date")
    public ResponseEntity<ApiResponse> getProjectByCreatedDate(
            @RequestParam("username") String userName,
            @RequestParam("created_date") LocalDateTime createdDate) {
        try {
            return ResponseEntity.ok(new ApiResponse("Project with created date "
                    + createdDate + " is found.", projectService.getProjectsByCreatedDate(userName, createdDate)));
        } catch (ResourceNotFoundException e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (DeniedUserException e){
            log.error(String.valueOf(e));
            return ResponseEntity.status(FORBIDDEN).body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found", null));
        }
    }

    @GetMapping("get/attachments")
    public ResponseEntity<ApiResponse> getAttachments(
            @RequestParam("id") Long projectId
    ) {
        try {
            return ResponseEntity.ok(new ApiResponse("Get Attachment Successfully", projectService.getProjectAttachments(projectId)));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found", null));
        }
    }

    @GetMapping("attachment/download/")
    public ResponseEntity<byte[]> downloadAttachment(
            @RequestParam("id") Long attachmentId
    ) {
        try {
            ProjectAttachment attachment = projectService.getFilePath(attachmentId);
            File file = new File(attachment.getFilePath() + "\\" + attachment.getFilename());
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + attachment.getFilename() + "\"")
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .body(fileContent);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
