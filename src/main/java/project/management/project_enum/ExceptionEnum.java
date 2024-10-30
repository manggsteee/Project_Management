package project.management.project_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@AllArgsConstructor
@Getter
public enum ExceptionEnum {
    APPLICATION_EXCEPTION(9999,"Application has some problems", INTERNAL_SERVER_ERROR),
    PROJECT_NOT_FOUND(1100,"Project not found", NOT_FOUND),
    TASK_NOT_FOUND(1200,"Task not found" , NOT_FOUND),
    WORK_NOT_FOUND(1300,"Work not found" , NOT_FOUND),
    USER_NOT_FOUND(1400, "User not found" , NOT_FOUND),
    FILE_NOT_FOUND(1500, "File not found" , NOT_FOUND),
    DENIED_USER_FOUND_PROJECT(2100, "User doesn't have permission to access to this project", FORBIDDEN),
    DENIED_USER_COMMENT_PROJECT(2200,"User doesn't have permission to comment to this project" , FORBIDDEN),
    WRONG_COMMENT_TYPE(3100,"Wrong comment type" , BAD_REQUEST),
    USER_EXISTED(4100,"User already existed" , CONFLICT),
    INVALID_USER(4200,"Invalid user" , UNAUTHORIZED),
    UNAUTHENTICATED(4300,"Unauthenticated" , UNAUTHORIZED),;
    private final int code;
    private final String message;
    private final HttpStatus status;
}
