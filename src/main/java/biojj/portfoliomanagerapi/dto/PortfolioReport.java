package biojj.portfoliomanagerapi.dto;

import biojj.portfoliomanagerapi.model.ProjectStatus;

import java.math.BigDecimal;
import java.util.Map;

public record PortfolioReport(Map<ProjectStatus, Long> projectsByStatus, Map<ProjectStatus, BigDecimal> budgetByStatus,
                              Double averageClosedProjectDurationDays, long uniqueAllocatedMembers) {
}
