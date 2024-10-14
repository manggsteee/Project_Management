package project.management.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import project.management.project_enum.ProjectPriority;

@Data
@EqualsAndHashCode(callSuper=true)
public class WorkRequest extends Request {
    private ProjectPriority priority;
    private String memberName;
}
