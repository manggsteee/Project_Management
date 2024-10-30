package project.management.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import project.management.project_enum.ExceptionEnum;
import project.management.response.ApiResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandling {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handleException(Exception exception) {
        log.error("Exception: {}", exception.getMessage(), exception);
        ExceptionEnum exceptionEnum = ExceptionEnum.APPLICATION_EXCEPTION;
        return ResponseEntity.status(exceptionEnum.getStatus()).body(
                ApiResponse.builder()
                        .code(exceptionEnum.getCode())
                        .message(exceptionEnum.getMessage())
                        .build());
    }

    @ExceptionHandler(value = ApplicationException.class)
    ResponseEntity<ApiResponse> handleApplicationException(ApplicationException exception) {
        log.error("ApplicationException: {}", exception.getMessage(), exception);
        ExceptionEnum exceptionEnum = exception.getExceptionEnum();
        return ResponseEntity.status(exceptionEnum.getStatus()).body(
                ApiResponse.builder()
                        .code(exceptionEnum.getCode())
                        .message(exceptionEnum.getMessage())
                        .build());
    }
}
