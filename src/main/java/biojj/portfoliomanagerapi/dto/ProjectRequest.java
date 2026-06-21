package biojj.portfoliomanagerapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record ProjectRequest(@NotBlank @Size(max = 150) String name, @NotNull LocalDate startDate,
                             @NotNull LocalDate expectedEndDate,
                             LocalDate actualEndDate, @NotNull @DecimalMin("0.01") BigDecimal totalBudget,
                             @NotBlank @Size(max = 2000) String description,
                             @NotNull Long managerId, @NotNull @Size(min = 1, max = 10) Set<Long> memberIds) {
}
