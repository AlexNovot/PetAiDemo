package org.aleksvander.petaidemo.petaidemo.config;

import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerResponseDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetResponseDto;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitResponseDto;
import org.aleksvander.petaidemo.petaidemo.repository.OwnerRepository;
import org.aleksvander.petaidemo.petaidemo.service.OwnerService;
import org.aleksvander.petaidemo.petaidemo.service.PetService;
import org.aleksvander.petaidemo.petaidemo.service.VisitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private OwnerService ownerService;

    @Mock
    private PetService petService;

    @Mock
    private VisitService visitService;

    @Test
    void run_databaseAlreadyHasData_skipsSeeding() throws Exception {
        when(ownerRepository.count()).thenReturn(5L);
        DataSeeder seeder = new DataSeeder(ownerRepository, ownerService, petService, visitService);

        seeder.run();

        verify(ownerService, never()).create(any());
        verify(petService, never()).create(any());
        verify(visitService, never()).create(any());
    }

    @Test
    void run_emptyDatabase_seedsOwnersAndPets() throws Exception {
        when(ownerRepository.count()).thenReturn(0L);
        when(ownerService.create(any(OwnerRequestDto.class)))
                .thenReturn(new OwnerResponseDto(1L, "Ivan", "Petrov", "ivan@example.com", "+79001234567"));
        when(petService.create(any(PetRequestDto.class)))
                .thenReturn(new PetResponseDto(1L, "Rex", org.aleksvander.petaidemo.petaidemo.entity.Species.DOG,
                        "Labrador", java.time.LocalDate.of(2020, 1, 1), 1L));
        when(visitService.create(any(VisitRequestDto.class)))
                .thenReturn(new VisitResponseDto(1L, java.time.LocalDate.of(2024, 1, 1), "Checkup", "notes", 1L));

        DataSeeder seeder = new DataSeeder(ownerRepository, ownerService, petService, visitService);

        seeder.run();

        verify(ownerService, org.mockito.Mockito.times(10)).create(any(OwnerRequestDto.class));
        verify(petService, org.mockito.Mockito.atLeastOnce()).create(any(PetRequestDto.class));
        verify(visitService, org.mockito.Mockito.atLeastOnce()).create(any(VisitRequestDto.class));
    }
}
