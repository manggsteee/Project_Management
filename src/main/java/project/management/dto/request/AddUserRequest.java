package project.management.dto.request;

import lombok.Data;
import project.management.project_enum.UserEnum;

@Data
public class AddUserRequest {
    private String username;
    private String password;
    private String fullName;
    private UserEnum userEnum;
}
