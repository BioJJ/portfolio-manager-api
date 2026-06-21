package biojj.portfoliomanagerapi.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalBudget;
    @Column(nullable = false, length = 2000)
    private String description;
    @Column(nullable = false)
    private Long managerId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.EM_ANALISE;
    @ElementCollection
    @CollectionTable(name = "project_members", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "member_id", nullable = false)
    private Set<Long> memberIds = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        name = v;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate v) {
        startDate = v;
    }

    public LocalDate getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(LocalDate v) {
        expectedEndDate = v;
    }

    public LocalDate getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDate v) {
        actualEndDate = v;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal v) {
        totalBudget = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        description = v;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long v) {
        managerId = v;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus v) {
        status = v;
    }

    public Set<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(Set<Long> v) {
        memberIds = new HashSet<>(v);
    }
}
