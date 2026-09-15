package org.aleksvander.petaidemo.petaidemo.mapper;

import org.aleksvander.petaidemo.petaidemo.dto.pet.PetRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Owner;
import org.aleksvander.petaidemo.petaidemo.entity.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", source = "owner")
    Pet toEntity(PetRequestDto dto, Owner owner);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", source = "owner")
    void updateEntity(@MappingTarget Pet pet, PetRequestDto dto, Owner owner);

    @Mapping(target = "ownerId", source = "owner.id")
    PetResponseDto toDto(Pet pet);
}
