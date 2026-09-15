package org.aleksvander.petaidemo.petaidemo.controller;

import jakarta.validation.Valid;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetResponseDto;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitResponseDto;
import org.aleksvander.petaidemo.petaidemo.service.PetService;
import org.aleksvander.petaidemo.petaidemo.service.VisitService;
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
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;
    private final VisitService visitService;

    public PetController(PetService petService, VisitService visitService) {
        this.petService = petService;
        this.visitService = visitService;
    }

    @GetMapping
    public List<PetResponseDto> getAll() {
        return petService.getAll();
    }

    @GetMapping("/{id}")
    public PetResponseDto getById(@PathVariable Long id) {
        return petService.getById(id);
    }

    @GetMapping("/{id}/visits")
    public List<VisitResponseDto> getVisits(@PathVariable Long id) {
        return visitService.getByPet(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponseDto create(@Valid @RequestBody PetRequestDto dto) {
        return petService.create(dto);
    }

    @PutMapping("/{id}")
    public PetResponseDto update(@PathVariable Long id, @Valid @RequestBody PetRequestDto dto) {
        return petService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        petService.delete(id);
    }
}
