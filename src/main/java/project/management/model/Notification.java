package project.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "project_notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "getter_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private String notification;
    private LocalDateTime notificationTime;
    @PrePersist
    protected void onCreate() {
        notificationTime = LocalDateTime.now();
    }

    public Notification(User user, String notification) {
        this.user = user;
        this.notification = notification;
    }
}
