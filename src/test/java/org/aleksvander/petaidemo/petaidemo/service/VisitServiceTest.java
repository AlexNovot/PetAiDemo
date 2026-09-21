package org.aleksvander.petaidemo.petaidemo.service;

import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Pet;
import org.aleksvander.petaidemo.petaidemo.entity.Visit;
import org.aleksvander.petaidemo.petaidemo.exception.ResourceNotFoundException;
import org.aleksvander.petaidemo.petaidemo.mapper.VisitMapper;
import org.aleksvander.petaidemo.petaidemo.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private VisitMapper visitMapper;

    @Mock
    private PetService petService;

    @InjectMocks
    private VisitService visitService;

    private Pet pet;
    private Visit visit;
    private VisitRequestDto requestDto;
    private VisitResponseDto responseDto;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(10L);

        visit = new Visit();
        visit.setId(100L);
        visit.setVersion(0L);
        visit.setVisitDate(LocalDate.of(2024, 5, 1));
        visit.setDiagnosis("Healthy checkup");
        visit.setNotes("All good");
        visit.setPet(pet);

        requestDto = new VisitRequestDto(LocalDate.of(2024, 5, 1), "Healthy checkup", "All good", 10L, 0L);
        responseDto = new VisitResponseDto(100L, LocalDate.of(2024, 5, 1), "Healthy checkup", "All good", 10L, 0L);
    }

    @Test
    void getAll_returnsMappedVisits() {
        when(visitRepository.findAll()).thenReturn(List.of(visit));
        when(visitMapper.toDto(visit)).thenReturn(responseDto);

        assertThat(visitService.getAll()).containsExactly(responseDto);
    }

    @Test
    void getByPet_returnsVisitsForThatPet() {
        when(visitRepository.findByPetId(10L)).thenReturn(List.of(visit));
        when(visitMapper.toDto(visit)).thenReturn(responseDto);

        assertThat(visitService.getByPet(10L)).containsExactly(responseDto);
    }

    @Test
    void getById_found_returnsDto() {
        when(visitRepository.findById(100L)).thenReturn(Optional.of(visit));
        when(visitMapper.toDto(visit)).thenReturn(responseDto);

        assertThat(visitService.getById(100L)).isEqualTo(responseDto);
    }

    @Test
    void getById_notFound_throws() {
        when(visitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> visitService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_petFound_savesVisitAndReturnsDto() {
        when(petService.findPetOrThrow(10L)).thenReturn(pet);
        when(visitMapper.toEntity(requestDto, pet)).thenReturn(visit);
        when(visitRepository.save(visit)).thenReturn(visit);
        when(visitMapper.toDto(visit)).thenReturn(responseDto);

        VisitResponseDto result = visitService.create(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(visitRepository).save(visit);
    }

    @Test
    void create_petNotFound_propagatesExceptionWithoutSaving() {
        when(petService.findPetOrThrow(10L)).thenThrow(new ResourceNotFoundException("Pet not found: 10"));

        assertThatThrownBy(() -> visitService.create(requestDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(visitRepository, never()).save(any());
    }

    @Test
    void update_found_updatesEntityAndReturnsDto() {
        when(visitRepository.findById(100L)).thenReturn(Optional.of(visit));
        when(petService.findPetOrThrow(10L)).thenReturn(pet);
        when(visitMapper.toDto(visit)).thenReturn(responseDto);

        VisitResponseDto result = visitService.update(100L, requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(visitMapper).updateEntity(visit, requestDto, pet);
    }

    @Test
    void update_visitNotFound_throwsAndSkipsPetLookup() {
        when(visitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> visitService.update(999L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(petService, never()).findPetOrThrow(any());
    }

    @Test
    void delete_found_deletesById() {
        when(visitRepository.existsById(100L)).thenReturn(true);

        visitService.delete(100L);

        verify(visitRepository, times(1)).deleteById(100L);
    }

    @Test
    void delete_notFound_throws() {
        when(visitRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> visitService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(visitRepository, never()).deleteById(any());
    }

    @Test
    void update_staleVersion_throwsOptimisticLockAndDoesNotUpdate() {
        when(visitRepository.findById(100L)).thenReturn(Optional.of(visit));
        visit.setVersion(5L);

        assertThatThrownBy(() -> visitService.update(100L, requestDto))
                .isInstanceOf(org.springframework.orm.ObjectOptimisticLockingFailureException.class);
        verify(visitMapper, never()).updateEntity(any(), any(), any());
    }

    @Test
    void update_missingVersion_throwsVersionRequired() {
        when(visitRepository.findById(100L)).thenReturn(Optional.of(visit));
        var noVersion = new org.aleksvander.petaidemo.petaidemo.dto.visit.VisitRequestDto(LocalDate.of(2024, 5, 1), null, null, 10L, null);

        assertThatThrownBy(() -> visitService.update(100L, noVersion))
                .isInstanceOf(org.aleksvander.petaidemo.petaidemo.exception.VersionRequiredException.class);
    }
}
