package biojj.portfoliomanagerapi.service;

import biojj.portfoliomanagerapi.dto.MemberRequest;
import biojj.portfoliomanagerapi.dto.PortfolioReport;
import biojj.portfoliomanagerapi.dto.ProjectRequest;
import biojj.portfoliomanagerapi.dto.ProjectResponse;
import biojj.portfoliomanagerapi.exception.BusinessException;
import biojj.portfoliomanagerapi.model.Project;
import biojj.portfoliomanagerapi.model.ProjectStatus;
import biojj.portfoliomanagerapi.model.RiskLevel;
import biojj.portfoliomanagerapi.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectServiceTest {
    private Project stored;
    private long activeProjects;
    private ProjectService service;

    @BeforeEach
    void setUp() {
        MockMemberService mock = new MockMemberService();
        mock.create(new MemberRequest("Ana", "funcionário"));
        ProjectRepository repo = (ProjectRepository) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ProjectRepository.class}, (p, m, a) -> switch (m.getName()) {
            case "save" -> {
                stored = (Project) a[0];
                yield stored;
            }
            case "findById" -> Optional.ofNullable(stored);
            case "countActiveProjectsForNewMember", "countActiveProjectsForMember" -> activeProjects;
            case "delete" -> {
                stored = null;
                yield null;
            }
            case "findAll" ->
                    a == null || a.length == 0 ? (stored == null ? List.of() : List.of(stored)) : new org.springframework.data.domain.PageImpl<>(stored == null ? List.of() : List.of(stored));
            default -> throw new UnsupportedOperationException(m.getName());
        });
        service = new ProjectService(repo, new MemberGateway(mock));
    }

    private ProjectRequest request(BigDecimal budget, LocalDate start, LocalDate end) {
        return new ProjectRequest("Projeto", start, end, null, budget, "Descrição", 1L, Set.of(1L));
    }

    private Project project(BigDecimal budget, LocalDate start, LocalDate end) {
        Project p = new Project();
        p.setTotalBudget(budget);
        p.setStartDate(start);
        p.setExpectedEndDate(end);
        return p;
    }

    @Test
    void calculatesLowMediumAndHighRisk() {
        assertThat(service.risk(project(new BigDecimal("100000"), LocalDate.of(2026, 1, 1), LocalDate.of(2026, 4, 1)))).isEqualTo(RiskLevel.BAIXO);
        assertThat(service.risk(project(new BigDecimal("100001"), LocalDate.of(2026, 1, 1), LocalDate.of(2026, 4, 1)))).isEqualTo(RiskLevel.MEDIO);
        assertThat(service.risk(project(new BigDecimal("500001"), LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1)))).isEqualTo(RiskLevel.ALTO);
    }

    @Test
    void createsProjectAfterEmployeeAndAllocationValidation() {
        ProjectResponse result = service.create(request(BigDecimal.TEN, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1)));
        assertThat(result.status()).isEqualTo(ProjectStatus.EM_ANALISE);
        assertThat(result.riskLevel()).isEqualTo(RiskLevel.BAIXO);
        assertThat(stored.getMemberIds()).containsExactly(1L);
    }

    @Test
    void refusesInvalidDatesAndOverallocatedMembers() {
        assertThatThrownBy(() -> service.create(request(BigDecimal.ONE, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)))).isInstanceOf(BusinessException.class).hasMessageContaining("previsão");
        activeProjects = 3;
        assertThatThrownBy(() -> service.create(request(BigDecimal.ONE, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1)))).isInstanceOf(BusinessException.class).hasMessageContaining("3 projetos");
    }

    @Test
    void statusOnlyMovesToNextOrCancellation() {
        service.create(request(BigDecimal.ONE, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1)));
        assertThat(service.changeStatus(1L, ProjectStatus.ANALISE_REALIZADA).status()).isEqualTo(ProjectStatus.ANALISE_REALIZADA);
        assertThatThrownBy(() -> service.changeStatus(1L, ProjectStatus.INICIADO)).isInstanceOf(BusinessException.class);
        assertThat(service.changeStatus(1L, ProjectStatus.CANCELADO).status()).isEqualTo(ProjectStatus.CANCELADO);
    }

    @Test
    void refusesDeletionForStartedProjects() {
        service.create(request(BigDecimal.ONE, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1)));
        stored.setStatus(ProjectStatus.INICIADO);
        assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(BusinessException.class);
    }

    @Test
    void updatesFindsSearchesAndDeletesCancelableProject() {
        service.create(request(BigDecimal.ONE, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1)));
        ProjectResponse updated = service.update(1L, request(new BigDecimal("200000"), LocalDate.of(2026, 1, 1), LocalDate.of(2026, 5, 1)));
        assertThat(updated.riskLevel()).isEqualTo(RiskLevel.MEDIO);
        assertThat(service.find(1L).totalBudget()).isEqualByComparingTo("200000");
        assertThat(service.search("pro", null, null, null, org.springframework.data.domain.PageRequest.of(0, 10)).getTotalElements()).isOne();
        service.changeStatus(1L, ProjectStatus.CANCELADO);
        service.delete(1L);
        assertThatThrownBy(() -> service.find(1L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void reportCalculatesClosedDuration() {
        service.create(new ProjectRequest("Projeto", LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), LocalDate.now(), BigDecimal.ONE, "Descrição", 1L, Set.of(1L)));
        stored.setStatus(ProjectStatus.ENCERRADO);
        assertThat(service.report().averageClosedProjectDurationDays()).isEqualTo(5d);
    }

    @Test
    void providesPortfolioSummary() {
        service.create(request(new BigDecimal("50"), LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1)));
        PortfolioReport report = service.report();
        assertThat(report.projectsByStatus()).containsEntry(ProjectStatus.EM_ANALISE, 1L);
        assertThat(report.budgetByStatus()).containsEntry(ProjectStatus.EM_ANALISE, new BigDecimal("50"));
        assertThat(report.uniqueAllocatedMembers()).isOne();
    }
}
