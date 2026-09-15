package org.aleksvander.petaidemo.petaidemo.mapper;

import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OwnerMapper {

    Owner toEntity(OwnerRequestDto dto);

    void updateEntity(@MappingTarget Owner owner, OwnerRequestDto dto);

    OwnerResponseDto toDto(Owner owner);
}
