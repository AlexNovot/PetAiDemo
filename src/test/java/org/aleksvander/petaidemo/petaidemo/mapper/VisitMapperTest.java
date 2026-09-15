package org.aleksvander.petaidemo.petaidemo.mapper;

import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Pet;
import org.aleksvander.petaidemo.petaidemo.entity.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class VisitMapperTest {

    private VisitMapper visitMapper;

    @BeforeEach
    void setUp() {
        visitMapper = new VisitMapperImpl();
    }

    @Test
    void toEntity_mapsFieldsAndPet_doesNotLeakPetIdIntoVisitId() {
        Pet pet = new Pet();
        pet.setId(42L);

        VisitRequestDto dto = new VisitRequestDto(LocalDate.of(2024, 5, 1), "Checkup", "notes", 42L);

        Visit visit = visitMapper.toEntity(dto, pet);

        // Regression test: id must NOT be copied from pet.id just because both fields are named "id".
        assertThat(visit.getId()).isNull();
        assertThat(visit.getVisitDate()).isEqualTo(LocalDate.of(2024, 5, 1));
        assertThat(visit.getDiagnosis()).isEqualTo("Checkup");
        assertThat(visit.getNotes()).isEqualTo("notes");
        assertThat(visit.getPet()).isSameAs(pet);
    }

    @Test
    void updateEntity_updatesFieldsAndPet_preservesExistingVisitId() {
        Visit visit = new Visit();
        visit.setId(7L);

        Pet newPet = new Pet();
        newPet.setId(1L);

        VisitRequestDto dto = new VisitRequestDto(LocalDate.of(2023, 3, 3), "Vaccination", "ok", 1L);

        visitMapper.updateEntity(visit, dto, newPet);

        // Regression test: existing visit id must survive the update, not be overwritten by pet.id.
        assertThat(visit.getId()).isEqualTo(7L);
        assertThat(visit.getVisitDate()).isEqualTo(LocalDate.of(2023, 3, 3));
        assertThat(visit.getDiagnosis()).isEqualTo("Vaccination");
        assertThat(visit.getNotes()).isEqualTo("ok");
        assertThat(visit.getPet()).isSameAs(newPet);
    }

    @Test
    void toDto_mapsPetIdFromPetAssociation() {
        Pet pet = new Pet();
        pet.setId(3L);

        Visit visit = new Visit();
        visit.setId(100L);
        visit.setVisitDate(LocalDate.of(2024, 5, 1));
        visit.setDiagnosis("Checkup");
        visit.setNotes("notes");
        visit.setPet(pet);

        VisitResponseDto dto = visitMapper.toDto(visit);

        assertThat(dto).isEqualTo(new VisitResponseDto(100L, LocalDate.of(2024, 5, 1), "Checkup", "notes", 3L));
    }
}
