package project.management.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
public class ProjectRequest extends Request {
    private List<MemberRolesRequest> memberRoles;
}
