package project.management.model.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.management.model.User;
import project.management.model.Work;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "work_comment")
public class WorkComment extends Comment{
    @ManyToOne
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    public WorkComment(String comment, User user, Work work) {
        super(comment, user);
        this.work = work;
    }
}
