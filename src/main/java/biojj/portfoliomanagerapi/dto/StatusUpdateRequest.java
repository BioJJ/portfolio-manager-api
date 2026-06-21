package biojj.portfoliomanagerapi.dto;

import biojj.portfoliomanagerapi.model.ProjectStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull ProjectStatus status) {
}
