package project.management.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.management.exception.ResourceNotFoundException;
import project.management.request.ProjectRequest;
import project.management.response.ApiResponse;
import project.management.service.ProjectService;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
        }catch (Exception e) {
                log.error(String.valueOf(e));
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Some exceptions have been found",null));
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
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found",null));
        }
    }

    @DeleteMapping("{projectId}/delete")
    public ResponseEntity<ApiResponse> deleteProject(
            @PathVariable Long projectId
    ){
        try {
            projectService.deleteProject(projectId);
            return ResponseEntity.ok(new ApiResponse("Delete Project Successfully",null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found",null));
        }
    }
}
