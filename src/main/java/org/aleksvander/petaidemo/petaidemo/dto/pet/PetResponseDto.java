package org.aleksvander.petaidemo.petaidemo.dto.pet;

import org.aleksvander.petaidemo.petaidemo.entity.Species;

import java.time.LocalDate;

public record PetResponseDto(
        Long id,
        String name,
        Species species,
        String breed,
        LocalDate birthDate,
        Long ownerId
) {
}
