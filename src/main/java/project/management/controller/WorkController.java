package project.management.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.management.exception.ResourceNotFoundException;
import project.management.request.WorkRequest;
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
    public ResponseEntity<ApiResponse> createWork(
            @PathVariable Long taskId,
            @RequestPart("work_informations") WorkRequest request,
            @RequestPart(value = "description_files",
                    required = false) List<MultipartFile> files) {
        try {
            return ResponseEntity.ok(new ApiResponse("Create Work Successfully",
                    workService.creatework(taskId, request, files)));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Some Exceptions have been found",null));
        }
    }

    @PutMapping("/{workId}/update")
    public ResponseEntity<ApiResponse> updateWork(
            @PathVariable Long workId,
            @RequestPart("update_informations") WorkRequest request,
            @RequestPart(value = "update_attachments",
            required = false) List<MultipartFile> files
    ){
        try {
            return ResponseEntity.ok(new ApiResponse("Update Work Successfully",
                    workService.updateWork(workId, request, files)));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Some Exceptions have been found",null));
        }
    }

    @DeleteMapping("{workId}/delete")
    public ResponseEntity<ApiResponse> deleteWork(
            @PathVariable Long workId
    ){
        try {
            workService.deleteWork(workId);
            return ResponseEntity.ok(new ApiResponse("Delete Work Successfully", null));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Some Exceptions have been found",null));
        }
    }
}
