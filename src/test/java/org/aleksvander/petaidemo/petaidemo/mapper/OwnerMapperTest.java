package org.aleksvander.petaidemo.petaidemo.mapper;

import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Owner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerMapperTest {

    private OwnerMapper ownerMapper;

    @BeforeEach
    void setUp() {
        ownerMapper = new OwnerMapperImpl();
    }

    @Test
    void toEntity_mapsAllFieldsAndLeavesIdNull() {
        OwnerRequestDto dto = new OwnerRequestDto("Ivan", "Petrov", "ivan@example.com", "+79001234567");

        Owner owner = ownerMapper.toEntity(dto);

        assertThat(owner.getId()).isNull();
        assertThat(owner.getFirstName()).isEqualTo("Ivan");
        assertThat(owner.getLastName()).isEqualTo("Petrov");
        assertThat(owner.getEmail()).isEqualTo("ivan@example.com");
        assertThat(owner.getPhone()).isEqualTo("+79001234567");
    }

    @Test
    void updateEntity_overwritesFieldsButKeepsId() {
        Owner owner = new Owner();
        owner.setId(5L);
        owner.setFirstName("Old");

        OwnerRequestDto dto = new OwnerRequestDto("New", "Name", "new@example.com", "+70000000000");

        ownerMapper.updateEntity(owner, dto);

        assertThat(owner.getId()).isEqualTo(5L);
        assertThat(owner.getFirstName()).isEqualTo("New");
        assertThat(owner.getLastName()).isEqualTo("Name");
        assertThat(owner.getEmail()).isEqualTo("new@example.com");
        assertThat(owner.getPhone()).isEqualTo("+70000000000");
    }

    @Test
    void toDto_mapsAllFields() {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setFirstName("Ivan");
        owner.setLastName("Petrov");
        owner.setEmail("ivan@example.com");
        owner.setPhone("+79001234567");

        OwnerResponseDto dto = ownerMapper.toDto(owner);

        assertThat(dto).isEqualTo(new OwnerResponseDto(1L, "Ivan", "Petrov", "ivan@example.com", "+79001234567"));
    }
}
