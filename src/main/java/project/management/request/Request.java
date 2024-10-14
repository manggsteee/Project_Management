package project.management.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import project.management.project_enum.ProjectStatus;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
public abstract class Request {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ProjectStatus status;
}
