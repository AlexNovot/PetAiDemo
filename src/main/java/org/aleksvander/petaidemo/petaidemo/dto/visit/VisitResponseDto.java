package org.aleksvander.petaidemo.petaidemo.dto.visit;

import java.time.LocalDate;

public record VisitResponseDto(
        Long id,
        LocalDate visitDate,
        String diagnosis,
        String notes,
        Long petId
) {
}
