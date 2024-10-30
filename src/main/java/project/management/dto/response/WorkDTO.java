package project.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import project.management.project_enum.ProjectPriority;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class WorkDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ProjectPriority priority;
    private List<AttachmentDTO> workAttachments;
    private String member;
}
