package biojj.portfoliomanagerapi.dto;

import biojj.portfoliomanagerapi.model.ProjectStatus;
import biojj.portfoliomanagerapi.model.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record ProjectResponse(Long id, String name, LocalDate startDate, LocalDate expectedEndDate,
                              LocalDate actualEndDate,
                              BigDecimal totalBudget, String description, Long managerId, ProjectStatus status,
                              RiskLevel riskLevel, Set<Long> memberIds) {
}
