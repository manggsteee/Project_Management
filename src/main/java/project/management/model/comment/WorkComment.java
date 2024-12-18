package project.management.model.comment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import project.management.model.Work;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "work_comment")
public class WorkComment extends Comment{
    @ManyToOne
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;
}
