package org.aleksvander.petaidemo.petaidemo.mapper;

import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Pet;
import org.aleksvander.petaidemo.petaidemo.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VisitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "pet", source = "pet")
    Visit toEntity(VisitRequestDto dto, Pet pet);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "pet", source = "pet")
    void updateEntity(@MappingTarget Visit visit, VisitRequestDto dto, Pet pet);

    @Mapping(target = "petId", source = "pet.id")
    VisitResponseDto toDto(Visit visit);
}
