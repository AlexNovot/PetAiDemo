package org.aleksvander.petaidemo.petaidemo.service;

import org.aleksvander.petaidemo.petaidemo.dto.pet.PetRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Owner;
import org.aleksvander.petaidemo.petaidemo.entity.Pet;
import org.aleksvander.petaidemo.petaidemo.exception.ResourceNotFoundException;
import org.aleksvander.petaidemo.petaidemo.exception.VersionChecker;
import org.aleksvander.petaidemo.petaidemo.mapper.PetMapper;
import org.aleksvander.petaidemo.petaidemo.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;
    private final OwnerService ownerService;

    public PetService(PetRepository petRepository, PetMapper petMapper, OwnerService ownerService) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
        this.ownerService = ownerService;
    }

    @Transactional(readOnly = true)
    public List<PetResponseDto> getAll() {
        return petRepository.findAll().stream().map(petMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<PetResponseDto> getByOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId).stream().map(petMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PetResponseDto getById(Long id) {
        return petMapper.toDto(findPetOrThrow(id));
    }

    public PetResponseDto create(PetRequestDto dto) {
        Owner owner = ownerService.findOwnerOrThrow(dto.ownerId());
        Pet saved = petRepository.save(petMapper.toEntity(dto, owner));
        return petMapper.toDto(saved);
    }

    public PetResponseDto update(Long id, PetRequestDto dto) {
        Pet pet = findPetOrThrow(id);
        VersionChecker.check(Pet.class, id, dto.version(), pet.getVersion());
        Owner owner = ownerService.findOwnerOrThrow(dto.ownerId());
        petMapper.updateEntity(pet, dto, owner);
        return petMapper.toDto(pet);
    }

    public void delete(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet not found: " + id);
        }
        petRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Pet findPetOrThrow(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found: " + id));
    }
}
