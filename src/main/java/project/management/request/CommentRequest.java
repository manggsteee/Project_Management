package project.management.request;

import lombok.Data;
import project.management.project_enum.ProjectType;

@Data
public class CommentRequest {
    private String comment;
    private String sender;
    private ProjectType type;
}
