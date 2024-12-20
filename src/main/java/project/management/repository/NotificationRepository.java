package project.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.management.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
