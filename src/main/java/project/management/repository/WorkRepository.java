package project.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.management.model.Work;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
}
