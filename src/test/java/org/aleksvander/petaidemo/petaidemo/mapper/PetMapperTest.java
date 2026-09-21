package org.aleksvander.petaidemo.petaidemo.mapper;

import org.aleksvander.petaidemo.petaidemo.dto.pet.PetRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Owner;
import org.aleksvander.petaidemo.petaidemo.entity.Pet;
import org.aleksvander.petaidemo.petaidemo.entity.Species;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PetMapperTest {

    private PetMapper petMapper;

    @BeforeEach
    void setUp() {
        petMapper = new PetMapperImpl();
    }

    @Test
    void toEntity_mapsFieldsAndOwner_doesNotLeakOwnerIdIntoPetId() {
        Owner owner = new Owner();
        owner.setId(99L);

        PetRequestDto dto = new PetRequestDto("Rex", Species.DOG, "Labrador", LocalDate.of(2020, 1, 1), 99L, null);

        Pet pet = petMapper.toEntity(dto, owner);

        // Regression test: id must NOT be copied from owner.id just because both fields are named "id".
        assertThat(pet.getId()).isNull();
        assertThat(pet.getName()).isEqualTo("Rex");
        assertThat(pet.getSpecies()).isEqualTo(Species.DOG);
        assertThat(pet.getBreed()).isEqualTo("Labrador");
        assertThat(pet.getBirthDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void updateEntity_updatesFieldsAndOwner_preservesExistingPetId() {
        Pet pet = new Pet();
        pet.setId(5L);

        Owner newOwner = new Owner();
        newOwner.setId(1L);

        PetRequestDto dto = new PetRequestDto("Bella", Species.CAT, "Siamese", LocalDate.of(2019, 6, 15), 1L, null);

        petMapper.updateEntity(pet, dto, newOwner);

        // Regression test: existing pet id must survive the update, not be overwritten by owner.id.
        assertThat(pet.getId()).isEqualTo(5L);
        assertThat(pet.getName()).isEqualTo("Bella");
        assertThat(pet.getSpecies()).isEqualTo(Species.CAT);
        assertThat(pet.getBreed()).isEqualTo("Siamese");
        assertThat(pet.getBirthDate()).isEqualTo(LocalDate.of(2019, 6, 15));
        assertThat(pet.getOwner()).isSameAs(newOwner);
    }

    @Test
    void toDto_mapsOwnerIdFromOwnerAssociation() {
        Owner owner = new Owner();
        owner.setId(7L);

        Pet pet = new Pet();
        pet.setId(10L);
        pet.setVersion(0L);
        pet.setName("Rex");
        pet.setSpecies(Species.DOG);
        pet.setBreed("Labrador");
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        pet.setOwner(owner);

        PetResponseDto dto = petMapper.toDto(pet);

        assertThat(dto).isEqualTo(new PetResponseDto(10L, "Rex", Species.DOG, "Labrador", LocalDate.of(2020, 1, 1), 7L, 0L));
    }
}
