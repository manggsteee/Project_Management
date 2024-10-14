package project.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import project.management.project_enum.MemberRoleEnum;

@Builder
@AllArgsConstructor
@Data
public class MemberRoleDTO {
    private String userName;
    private MemberRoleEnum role;
}
