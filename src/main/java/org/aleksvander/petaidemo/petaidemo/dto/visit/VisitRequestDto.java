package org.aleksvander.petaidemo.petaidemo.dto.visit;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VisitRequestDto(
        @NotNull LocalDate visitDate,
        String diagnosis,
        String notes,
        @NotNull Long petId,
        Long version
) {
}
