package org.aleksvander.petaidemo.petaidemo.dto.pet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.aleksvander.petaidemo.petaidemo.entity.Species;

import java.time.LocalDate;

public record PetRequestDto(
        @NotBlank String name,
        @NotNull Species species,
        String breed,
        LocalDate birthDate,
        @NotNull Long ownerId,
        Long version
) {
}
