package org.aleksvander.petaidemo.petaidemo.dto.owner;

public record OwnerResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Long version
) {
}
