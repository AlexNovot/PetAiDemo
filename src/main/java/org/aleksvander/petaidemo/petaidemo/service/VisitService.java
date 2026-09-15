package org.aleksvander.petaidemo.petaidemo.service;

import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Pet;
import org.aleksvander.petaidemo.petaidemo.entity.Visit;
import org.aleksvander.petaidemo.petaidemo.exception.ResourceNotFoundException;
import org.aleksvander.petaidemo.petaidemo.mapper.VisitMapper;
import org.aleksvander.petaidemo.petaidemo.repository.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VisitService {

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final PetService petService;

    public VisitService(VisitRepository visitRepository, VisitMapper visitMapper, PetService petService) {
        this.visitRepository = visitRepository;
        this.visitMapper = visitMapper;
        this.petService = petService;
    }

    @Transactional(readOnly = true)
    public List<VisitResponseDto> getAll() {
        return visitRepository.findAll().stream().map(visitMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<VisitResponseDto> getByPet(Long petId) {
        return visitRepository.findByPetId(petId).stream().map(visitMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public VisitResponseDto getById(Long id) {
        return visitMapper.toDto(findVisitOrThrow(id));
    }

    public VisitResponseDto create(VisitRequestDto dto) {
        Pet pet = petService.findPetOrThrow(dto.petId());
        Visit saved = visitRepository.save(visitMapper.toEntity(dto, pet));
        return visitMapper.toDto(saved);
    }

    public VisitResponseDto update(Long id, VisitRequestDto dto) {
        Visit visit = findVisitOrThrow(id);
        Pet pet = petService.findPetOrThrow(dto.petId());
        visitMapper.updateEntity(visit, dto, pet);
        return visitMapper.toDto(visit);
    }

    public void delete(Long id) {
        if (!visitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Visit not found: " + id);
        }
        visitRepository.deleteById(id);
    }

    private Visit findVisitOrThrow(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found: " + id));
    }
}
