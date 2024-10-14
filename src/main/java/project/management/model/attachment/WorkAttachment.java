package project.management.model.attachment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import project.management.model.Work;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "work_attachment")
public class WorkAttachment extends Attachment {
    @ManyToOne
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;
}
