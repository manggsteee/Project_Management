package project.management.model.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.management.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String comment;
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User user;
    private LocalDateTime sendingTime;

    @PrePersist
    public void setSendingTime() {
        this.sendingTime = LocalDateTime.now();
    }

    public Comment(String comment, User user) {
        this.comment = comment;
        this.user = user;
    }
}
