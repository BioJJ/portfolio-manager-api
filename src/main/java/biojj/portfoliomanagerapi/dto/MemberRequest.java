package biojj.portfoliomanagerapi.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberRequest(@NotBlank String name, @NotBlank String attribution) {
}
