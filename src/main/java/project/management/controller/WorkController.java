package project.management.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.management.dto.request.WorkRequest;
import project.management.response.ApiResponse;
import project.management.service.WorkService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/work")
public class WorkController {
    private final WorkService workService;

    @PostMapping("/{taskId}/create")
    public ApiResponse createWork(
            @PathVariable Long taskId,
            @RequestPart("work_informations") WorkRequest request,
            @RequestPart(value = "description_files", required = false) List<MultipartFile> files) {
        return ApiResponse.builder()
                .message("Create Work Successfully")
                .data(workService.createWork(taskId, request, files))
                .build();
    }

    @PutMapping("/{workId}/update")
    public ApiResponse updateWork(
            @PathVariable Long workId,
            @RequestPart("update_informations") WorkRequest request,
            @RequestPart(value = "update_attachments", required = false) List<MultipartFile> files) {
        return ApiResponse.builder()
                .message("Update Work Successfully")
                .data(workService.updateWork(workId, request, files))
                .build();
    }

    @DeleteMapping("{workId}/delete")
    public ApiResponse deleteWork(@PathVariable Long workId) {
        workService.deleteWork(workId);
        return ApiResponse.builder()
                .message("Delete Work Successfully")
                .data(null)
                .build();
    }
}