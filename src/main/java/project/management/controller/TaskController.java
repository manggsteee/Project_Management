package project.management.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.management.exception.ResourceNotFoundException;
import project.management.request.TaskRequest;
import project.management.response.ApiResponse;
import project.management.service.TaskService;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/task")
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/{projectId}/create")
    public ResponseEntity<ApiResponse> createTask(
            @PathVariable Long projectId,
            @RequestPart("task_informations") TaskRequest request,
            @RequestParam(value = "description_files",
                    required = false) List<MultipartFile> files) {
        try {
            return ResponseEntity.ok(new ApiResponse("Create Task Successfully",
                    taskService.createTask(projectId, request, files)));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Project not found",null));
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found",null));
        }
    }

    @PutMapping("{taskId}/update")
    public ResponseEntity<ApiResponse> updateTask(
            @PathVariable Long taskId,
            @RequestPart("update_informations") TaskRequest updateTaskRequest,
            @RequestPart(value = "update_attachments",
                    required = false) List<MultipartFile> files
    ){
        try {
            return ResponseEntity.ok(new ApiResponse("Update Task Successfully",
                    taskService.updateTask(taskId,updateTaskRequest,files)));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Task not found",null));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found",null)
            );
        }
    }

    @DeleteMapping("{taskId}/delete")
    public ResponseEntity<ApiResponse> deleteTask(
            @PathVariable Long taskId
    ){
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.ok(new ApiResponse("Delete Task Successfully",null));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Task not found",null));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Some exceptions have been found",null)
            );
        }
    }
}
