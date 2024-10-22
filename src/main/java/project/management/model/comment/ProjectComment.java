package project.management.model.comment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import project.management.model.Project;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "project_comment")
public class ProjectComment extends Comment{
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
