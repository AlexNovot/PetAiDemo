package org.aleksvander.petaidemo.petaidemo.controller;

import jakarta.validation.Valid;
import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerResponseDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetResponseDto;
import org.aleksvander.petaidemo.petaidemo.service.OwnerService;
import org.aleksvander.petaidemo.petaidemo.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;
    private final PetService petService;

    public OwnerController(OwnerService ownerService, PetService petService) {
        this.ownerService = ownerService;
        this.petService = petService;
    }

    @GetMapping
    public List<OwnerResponseDto> getAll() {
        return ownerService.getAll();
    }

    @GetMapping("/{id}")
    public OwnerResponseDto getById(@PathVariable Long id) {
        return ownerService.getById(id);
    }

    @GetMapping("/{id}/pets")
    public List<PetResponseDto> getPets(@PathVariable Long id) {
        return petService.getByOwner(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerResponseDto create(@Valid @RequestBody OwnerRequestDto dto) {
        return ownerService.create(dto);
    }

    @PutMapping("/{id}")
    public OwnerResponseDto update(@PathVariable Long id, @Valid @RequestBody OwnerRequestDto dto) {
        return ownerService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ownerService.delete(id);
    }
}
