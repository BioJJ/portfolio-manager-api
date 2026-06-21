package biojj.portfoliomanagerapi.service;

import biojj.portfoliomanagerapi.dto.PortfolioReport;
import biojj.portfoliomanagerapi.dto.ProjectRequest;
import biojj.portfoliomanagerapi.dto.ProjectResponse;
import biojj.portfoliomanagerapi.exception.BusinessException;
import biojj.portfoliomanagerapi.exception.NotFoundException;
import biojj.portfoliomanagerapi.model.Project;
import biojj.portfoliomanagerapi.model.ProjectStatus;
import biojj.portfoliomanagerapi.model.RiskLevel;
import biojj.portfoliomanagerapi.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {
    private static final Set<ProjectStatus> FINAL_STATUSES = Set.of(ProjectStatus.ENCERRADO, ProjectStatus.CANCELADO);
    private final ProjectRepository repository;
    private final MemberGateway members;

    public ProjectService(ProjectRepository repository, MemberGateway members) {
        this.repository = repository;
        this.members = members;
    }

    public ProjectResponse create(ProjectRequest request) {
        Project p = new Project();
        copy(request, p);
        validate(p, null);
        return response(repository.save(p));
    }

    public ProjectResponse update(Long id, ProjectRequest request) {
        Project p = get(id);
        copy(request, p);
        validate(p, id);
        return response(repository.save(p));
    }

    @Transactional(readOnly = true)
    public ProjectResponse find(Long id) {
        return response(get(id));
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> search(String name, ProjectStatus status, LocalDate startFrom, LocalDate startTo, Pageable page) {
        Specification<Project> spec = Specification.where(null);
        if (name != null && !name.isBlank())
            spec = spec.and((r, q, c) -> c.like(c.lower(r.get("name")), "%" + name.toLowerCase() + "%"));
        if (status != null) spec = spec.and((r, q, c) -> c.equal(r.get("status"), status));
        if (startFrom != null) spec = spec.and((r, q, c) -> c.greaterThanOrEqualTo(r.get("startDate"), startFrom));
        if (startTo != null) spec = spec.and((r, q, c) -> c.lessThanOrEqualTo(r.get("startDate"), startTo));
        return repository.findAll(spec, page).map(this::response);
    }

    public ProjectResponse changeStatus(Long id, ProjectStatus next) {
        Project p = get(id);
        if (!p.getStatus().mayTransitionTo(next))
            throw new BusinessException("Transição inválida de " + p.getStatus() + " para " + next);
        p.setStatus(next);
        if (next == ProjectStatus.ENCERRADO && p.getActualEndDate() == null) p.setActualEndDate(LocalDate.now());
        return response(repository.save(p));
    }

    public void delete(Long id) {
        Project p = get(id);
        if (p.getStatus() == ProjectStatus.INICIADO || p.getStatus() == ProjectStatus.EM_ANDAMENTO || p.getStatus() == ProjectStatus.ENCERRADO)
            throw new BusinessException("Projetos iniciados, em andamento ou encerrados não podem ser excluídos");
        repository.delete(p);
    }

    @Transactional(readOnly = true)
    public PortfolioReport report() {
        List<Project> all = repository.findAll();
        Map<ProjectStatus, Long> count = all.stream().collect(Collectors.groupingBy(Project::getStatus, () -> new EnumMap<>(ProjectStatus.class), Collectors.counting()));
        Map<ProjectStatus, BigDecimal> budget = all.stream().collect(Collectors.groupingBy(Project::getStatus, () -> new EnumMap<>(ProjectStatus.class), Collectors.reducing(BigDecimal.ZERO, Project::getTotalBudget, BigDecimal::add)));
        Double avg = all.stream().filter(p -> p.getStatus() == ProjectStatus.ENCERRADO && p.getActualEndDate() != null).mapToLong(p -> Duration.between(p.getStartDate().atStartOfDay(), p.getActualEndDate().atStartOfDay()).toDays()).average().isPresent() ? all.stream().filter(p -> p.getStatus() == ProjectStatus.ENCERRADO && p.getActualEndDate() != null).mapToLong(p -> Duration.between(p.getStartDate().atStartOfDay(), p.getActualEndDate().atStartOfDay()).toDays()).average().getAsDouble() : null;
        long unique = all.stream().flatMap(p -> p.getMemberIds().stream()).distinct().count();
        return new PortfolioReport(count, budget, avg, unique);
    }

    private Project get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Projeto não encontrado: " + id));
    }

    private void copy(ProjectRequest r, Project p) {
        p.setName(r.name());
        p.setStartDate(r.startDate());
        p.setExpectedEndDate(r.expectedEndDate());
        p.setActualEndDate(r.actualEndDate());
        p.setTotalBudget(r.totalBudget());
        p.setDescription(r.description());
        p.setManagerId(r.managerId());
        p.setMemberIds(r.memberIds());
    }

    private void validate(Project p, Long projectId) {
        if (p.getMemberIds().size() < 1 || p.getMemberIds().size() > 10)
            throw new BusinessException("Um projeto deve ter entre 1 e 10 membros");
        if (!p.getExpectedEndDate().isAfter(p.getStartDate()))
            throw new BusinessException("A previsão de término deve ser posterior à data de início");
        if (p.getActualEndDate() != null && p.getActualEndDate().isBefore(p.getStartDate()))
            throw new BusinessException("A data real de término não pode ser anterior ao início");
        members.find(p.getManagerId());
        for (Long memberId : p.getMemberIds()) {
            members.find(memberId);
            long active = projectId == null ? repository.countActiveProjectsForNewMember(memberId, FINAL_STATUSES) : repository.countActiveProjectsForMember(memberId, projectId, FINAL_STATUSES);
            if (active >= 3)
                throw new BusinessException("Membro " + memberId + " já está alocado em 3 projetos ativos");
        }
    }

    private ProjectResponse response(Project p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getStartDate(), p.getExpectedEndDate(), p.getActualEndDate(), p.getTotalBudget(), p.getDescription(), p.getManagerId(), p.getStatus(), risk(p), Set.copyOf(p.getMemberIds()));
    }

    RiskLevel risk(Project p) {
        long months = java.time.temporal.ChronoUnit.MONTHS.between(p.getStartDate(), p.getExpectedEndDate());
        if (p.getTotalBudget().compareTo(new BigDecimal("500000")) > 0 || months > 6) return RiskLevel.ALTO;
        if (p.getTotalBudget().compareTo(new BigDecimal("100000")) <= 0 && months <= 3) return RiskLevel.BAIXO;
        return RiskLevel.MEDIO;
    }
}
