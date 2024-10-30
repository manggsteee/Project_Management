package project.management.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.management.dto.request.TaskRequest;
import project.management.response.ApiResponse;
import project.management.service.TaskService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/task")
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/{projectId}/create")
    public ApiResponse createTask(
            @PathVariable Long projectId,
            @RequestPart("task_informations") TaskRequest request,
            @RequestPart(value = "description_files", required = false) List<MultipartFile> files) {
        return ApiResponse.builder()
                .message("Create Task Successfully")
                .data(taskService.createTask(projectId, request, files))
                .build();
    }

    @PutMapping("{taskId}/update")
    public ApiResponse updateTask(
            @PathVariable Long taskId,
            @RequestPart("update_informations") TaskRequest updateTaskRequest,
            @RequestPart(value = "update_attachments", required = false) List<MultipartFile> files) {
        return ApiResponse.builder()
                .message("Update Task Successfully")
                .data(taskService.updateTask(taskId, updateTaskRequest, files))
                .build();
    }

    @DeleteMapping("{taskId}/delete")
    public ApiResponse deleteTask(
            @PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ApiResponse.builder()
                .message("Delete Task Successfully")
                .data(null)
                .build();
    }
}

