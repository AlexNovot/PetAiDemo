package org.aleksvander.petaidemo.petaidemo.service;

import org.aleksvander.petaidemo.petaidemo.dto.pet.PetRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Owner;
import org.aleksvander.petaidemo.petaidemo.entity.Pet;
import org.aleksvander.petaidemo.petaidemo.entity.Species;
import org.aleksvander.petaidemo.petaidemo.exception.ResourceNotFoundException;
import org.aleksvander.petaidemo.petaidemo.mapper.PetMapper;
import org.aleksvander.petaidemo.petaidemo.repository.PetRepository;
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
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetMapper petMapper;

    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private PetService petService;

    private Owner owner;
    private Pet pet;
    private PetRequestDto requestDto;
    private PetResponseDto responseDto;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1L);

        pet = new Pet();
        pet.setId(10L);
        pet.setName("Rex");
        pet.setSpecies(Species.DOG);
        pet.setBreed("Labrador");
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        pet.setOwner(owner);

        requestDto = new PetRequestDto("Rex", Species.DOG, "Labrador", LocalDate.of(2020, 1, 1), 1L);
        responseDto = new PetResponseDto(10L, "Rex", Species.DOG, "Labrador", LocalDate.of(2020, 1, 1), 1L);
    }

    @Test
    void getAll_returnsMappedPets() {
        when(petRepository.findAll()).thenReturn(List.of(pet));
        when(petMapper.toDto(pet)).thenReturn(responseDto);

        assertThat(petService.getAll()).containsExactly(responseDto);
    }

    @Test
    void getByOwner_returnsPetsForThatOwner() {
        when(petRepository.findByOwnerId(1L)).thenReturn(List.of(pet));
        when(petMapper.toDto(pet)).thenReturn(responseDto);

        assertThat(petService.getByOwner(1L)).containsExactly(responseDto);
    }

    @Test
    void getById_found_returnsDto() {
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(petMapper.toDto(pet)).thenReturn(responseDto);

        assertThat(petService.getById(10L)).isEqualTo(responseDto);
    }

    @Test
    void getById_notFound_throws() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_ownerFound_savesPetAndReturnsDto() {
        when(ownerService.findOwnerOrThrow(1L)).thenReturn(owner);
        when(petMapper.toEntity(requestDto, owner)).thenReturn(pet);
        when(petRepository.save(pet)).thenReturn(pet);
        when(petMapper.toDto(pet)).thenReturn(responseDto);

        PetResponseDto result = petService.create(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(petRepository).save(pet);
    }

    @Test
    void create_ownerNotFound_propagatesExceptionWithoutSaving() {
        when(ownerService.findOwnerOrThrow(1L)).thenThrow(new ResourceNotFoundException("Owner not found: 1"));

        assertThatThrownBy(() -> petService.create(requestDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(petRepository, never()).save(any());
    }

    @Test
    void update_found_updatesEntityAndReturnsDto() {
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(ownerService.findOwnerOrThrow(1L)).thenReturn(owner);
        when(petMapper.toDto(pet)).thenReturn(responseDto);

        PetResponseDto result = petService.update(10L, requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(petMapper).updateEntity(pet, requestDto, owner);
    }

    @Test
    void update_petNotFound_throwsAndSkipsOwnerLookup() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.update(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(ownerService, never()).findOwnerOrThrow(any());
    }

    @Test
    void delete_found_deletesById() {
        when(petRepository.existsById(10L)).thenReturn(true);

        petService.delete(10L);

        verify(petRepository, times(1)).deleteById(10L);
    }

    @Test
    void delete_notFound_throws() {
        when(petRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> petService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(petRepository, never()).deleteById(any());
    }

    @Test
    void findPetOrThrow_notFound_throws() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.findPetOrThrow(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
