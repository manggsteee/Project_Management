package project.management.service;

import org.springframework.web.multipart.MultipartFile;
import project.management.dto.TaskDTO;
import project.management.request.TaskRequest;

import java.util.List;

public interface TaskServiceInterface {
     TaskDTO createTask(
            Long projectId,
            TaskRequest request,
            List<MultipartFile> files);

    TaskDTO updateTask(Long taskId, TaskRequest updateTaskRequest, List<MultipartFile> files);

    void deleteTask(Long taskId);

    //List<TaskDTO> getTasksByProjectId(Long projectId);
}
