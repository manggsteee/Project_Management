package project.management.service;

import org.springframework.web.multipart.MultipartFile;
import project.management.dto.response.WorkDTO;
import project.management.dto.request.WorkRequest;

import java.util.List;

public interface WorkInterface {
    WorkDTO createWork(Long taskId, WorkRequest request, List<MultipartFile> files);

    WorkDTO updateWork(Long workId, WorkRequest request, List<MultipartFile> files);

    void deleteWork(Long workId);

    void submitWork(Long id, List<MultipartFile> files);
}
