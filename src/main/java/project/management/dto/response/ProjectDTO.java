package project.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import project.management.project_enum.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class ProjectDTO{
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ProjectStatus status;
    private List<AttachmentDTO> projectAttachments;
    private List<MemberRoleDTO> memberRoles;
}
