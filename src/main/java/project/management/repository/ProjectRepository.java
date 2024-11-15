package project.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.management.model.Project;
import project.management.project_enum.ProjectStatus;

import java.time.LocalDateTime;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByName(String projectName, Pageable pageable);

    Page<Project> findByStartDate(LocalDateTime startDate, Pageable pageable);

    Page<Project> findByEndDate(LocalDateTime endDate, Pageable pageable);

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    Page<Project> findByCreatedAt(LocalDateTime createdDate, Pageable pageable);
}