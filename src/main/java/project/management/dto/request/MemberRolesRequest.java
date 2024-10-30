package project.management.dto.request;

import lombok.Builder;
import lombok.Data;
import project.management.project_enum.MemberRoleEnum;

@Data
@Builder
public class MemberRolesRequest {
    private String userName;
    private MemberRoleEnum role;
}
