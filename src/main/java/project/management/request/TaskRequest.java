package project.management.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import project.management.project_enum.ProjectPriority;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
public class TaskRequest extends Request {
    private ProjectPriority priority;
    private List<String> userNames;
}
