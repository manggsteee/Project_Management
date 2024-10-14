package project.management.request;

import lombok.Data;
import project.management.project_enum.MemberRoleEnum;

@Data
public class MemberRolesRequest {
    private String userName;
    private MemberRoleEnum role;
}
