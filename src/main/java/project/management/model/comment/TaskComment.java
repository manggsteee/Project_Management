package project.management.model.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.management.model.Task;
import project.management.model.User;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "task_comment")
public class TaskComment extends Comment{
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    public TaskComment(String comment, User user, Task task) {
        super(comment, user);
        this.task = task;
    }
}
