package org.aleksvander.petaidemo.petaidemo.service;

import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Owner;
import org.aleksvander.petaidemo.petaidemo.exception.ResourceNotFoundException;
import org.aleksvander.petaidemo.petaidemo.mapper.OwnerMapper;
import org.aleksvander.petaidemo.petaidemo.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;

    public OwnerService(OwnerRepository ownerRepository, OwnerMapper ownerMapper) {
        this.ownerRepository = ownerRepository;
        this.ownerMapper = ownerMapper;
    }

    @Transactional(readOnly = true)
    public List<OwnerResponseDto> getAll() {
        return ownerRepository.findAll().stream().map(ownerMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public OwnerResponseDto getById(Long id) {
        return ownerMapper.toDto(findOwnerOrThrow(id));
    }

    public OwnerResponseDto create(OwnerRequestDto dto) {
        Owner saved = ownerRepository.save(ownerMapper.toEntity(dto));
        return ownerMapper.toDto(saved);
    }

    public OwnerResponseDto update(Long id, OwnerRequestDto dto) {
        Owner owner = findOwnerOrThrow(id);
        ownerMapper.updateEntity(owner, dto);
        return ownerMapper.toDto(owner);
    }

    public void delete(Long id) {
        if (!ownerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Owner not found: " + id);
        }
        ownerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Owner findOwnerOrThrow(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + id));
    }
}
