package org.aleksvander.petaidemo.petaidemo.config;

import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerResponseDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetResponseDto;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitRequestDto;
import org.aleksvander.petaidemo.petaidemo.entity.Species;
import org.aleksvander.petaidemo.petaidemo.repository.OwnerRepository;
import org.aleksvander.petaidemo.petaidemo.service.OwnerService;
import org.aleksvander.petaidemo.petaidemo.service.PetService;
import org.aleksvander.petaidemo.petaidemo.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Seeds the database with demo data on first startup (skipped once owners already exist),
 * so the API is ready to explore without manual setup.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private record OwnerSeed(String firstName, String lastName, String email, String phone) {
    }

    private record PetSeed(String name, Species species, String breed, LocalDate birthDate) {
    }

    private record VisitSeed(LocalDate visitDate, String diagnosis, String notes) {
    }

    private static final List<OwnerSeed> OWNERS = List.of(
            new OwnerSeed("Ivan", "Petrov", "ivan.petrov@example.com", "+79001000001"),
            new OwnerSeed("Olga", "Sokolova", "olga.sokolova@example.com", "+79001000002"),
            new OwnerSeed("Sergey", "Volkov", "sergey.volkov@example.com", "+79001000003"),
            new OwnerSeed("Anna", "Kuznetsova", "anna.kuznetsova@example.com", "+79001000004"),
            new OwnerSeed("Dmitry", "Popov", "dmitry.popov@example.com", "+79001000005"),
            new OwnerSeed("Elena", "Morozova", "elena.morozova@example.com", "+79001000006"),
            new OwnerSeed("Pavel", "Fedorov", "pavel.fedorov@example.com", "+79001000007"),
            new OwnerSeed("Natalia", "Orlova", "natalia.orlova@example.com", "+79001000008"),
            new OwnerSeed("Andrey", "Zaitsev", "andrey.zaitsev@example.com", "+79001000009"),
            new OwnerSeed("Yulia", "Egorova", "yulia.egorova@example.com", "+79001000010")
    );

    private static final List<PetSeed> PETS = List.of(
            new PetSeed("Rex", Species.DOG, "Labrador", LocalDate.of(2020, 3, 12)),
            new PetSeed("Bella", Species.CAT, "British Shorthair", LocalDate.of(2019, 7, 4)),
            new PetSeed("Kesha", Species.BIRD, "Parrot", LocalDate.of(2022, 1, 20)),
            new PetSeed("Sharik", Species.DOG, "Husky", LocalDate.of(2021, 11, 2)),
            new PetSeed("Murka", Species.CAT, "Siamese", LocalDate.of(2018, 5, 30))
    );

    private static final List<VisitSeed> VISITS = List.of(
            new VisitSeed(LocalDate.of(2024, 2, 10), "Routine checkup", "Healthy, no concerns"),
            new VisitSeed(LocalDate.of(2024, 6, 21), "Vaccination", "Annual vaccination completed"),
            new VisitSeed(LocalDate.of(2025, 1, 15), "Dental cleaning", "Mild tartar removed")
    );

    private final OwnerRepository ownerRepository;
    private final OwnerService ownerService;
    private final PetService petService;
    private final VisitService visitService;

    public DataSeeder(OwnerRepository ownerRepository, OwnerService ownerService,
                       PetService petService, VisitService visitService) {
        this.ownerRepository = ownerRepository;
        this.ownerService = ownerService;
        this.petService = petService;
        this.visitService = visitService;
    }

    @Override
    public void run(String... args) {
        if (ownerRepository.count() > 0) {
            log.info("Database already contains data, skipping seed.");
            return;
        }

        log.info("Seeding database with demo data...");

        int petIndex = 0;
        int visitIndex = 0;
        int totalPets = 0;
        int totalVisits = 0;

        for (OwnerSeed ownerSeed : OWNERS) {
            OwnerResponseDto owner = ownerService.create(
                    new OwnerRequestDto(ownerSeed.firstName(), ownerSeed.lastName(), ownerSeed.email(), ownerSeed.phone())
            );

            int petsForOwner = 1 + (petIndex % 2);
            for (int i = 0; i < petsForOwner; i++) {
                PetSeed petSeed = PETS.get(petIndex % PETS.size());
                petIndex++;

                PetResponseDto pet = petService.create(new PetRequestDto(
                        petSeed.name(), petSeed.species(), petSeed.breed(), petSeed.birthDate(), owner.id()
                ));
                totalPets++;

                int visitsForPet = petIndex % 2;
                for (int v = 0; v < visitsForPet; v++) {
                    VisitSeed visitSeed = VISITS.get(visitIndex % VISITS.size());
                    visitIndex++;

                    visitService.create(new VisitRequestDto(
                            visitSeed.visitDate(), visitSeed.diagnosis(), visitSeed.notes(), pet.id()
                    ));
                    totalVisits++;
                }
            }
        }

        log.info("Seed complete: {} owners, {} pets, {} visits.", OWNERS.size(), totalPets, totalVisits);
    }
}
