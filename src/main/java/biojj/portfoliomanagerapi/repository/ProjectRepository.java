package biojj.portfoliomanagerapi.repository;

import biojj.portfoliomanagerapi.model.Project;
import biojj.portfoliomanagerapi.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    @Query("select count(p) from Project p join p.memberIds m where m=:memberId and p.id <> :projectId and p.status not in :finalStatuses")
    long countActiveProjectsForMember(@Param("memberId") Long memberId, @Param("projectId") Long projectId, @Param("finalStatuses") Collection<ProjectStatus> finalStatuses);

    @Query("select count(p) from Project p join p.memberIds m where m=:memberId and p.status not in :finalStatuses")
    long countActiveProjectsForNewMember(@Param("memberId") Long memberId, @Param("finalStatuses") Collection<ProjectStatus> finalStatuses);
}
