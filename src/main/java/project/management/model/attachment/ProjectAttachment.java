package project.management.model.attachment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import project.management.model.Project;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "project_attachment")
public class ProjectAttachment extends Attachment {
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
