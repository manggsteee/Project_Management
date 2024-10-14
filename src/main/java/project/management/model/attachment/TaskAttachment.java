package project.management.model.attachment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import project.management.model.Task;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "task_attachment")
public class TaskAttachment extends Attachment {
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
