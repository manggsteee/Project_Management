package project.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.management.model.Project;
import project.management.project_enum.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<List<Project>> getProjectByName(String projectName);

    Optional<List<Project>> getProjectByStartDate(LocalDateTime startDate);

    Optional<List<Project>> getProjectByEndDate(LocalDateTime endDate);

    Optional<List<Project>> getProjectByStatus(ProjectStatus status);

    Optional<List<Project>> getProjectByCreatedAt(LocalDateTime createdDate);
}
