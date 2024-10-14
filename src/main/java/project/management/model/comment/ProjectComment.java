package project.management.model.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.management.model.Project;
import project.management.model.User;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "project_comment")
public class ProjectComment extends Comment{
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public ProjectComment(String comment,
                          User user,
                          Project project) {
        super(comment, user);
        this.project = project;
    }
}
