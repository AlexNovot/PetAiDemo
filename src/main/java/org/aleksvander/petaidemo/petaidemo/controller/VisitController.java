package org.aleksvander.petaidemo.petaidemo.controller;

import jakarta.validation.Valid;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitResponseDto;
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
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping
    public List<VisitResponseDto> getAll() {
        return visitService.getAll();
    }

    @GetMapping("/{id}")
    public VisitResponseDto getById(@PathVariable Long id) {
        return visitService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitResponseDto create(@Valid @RequestBody VisitRequestDto dto) {
        return visitService.create(dto);
    }

    @PutMapping("/{id}")
    public VisitResponseDto update(@PathVariable Long id, @Valid @RequestBody VisitRequestDto dto) {
        return visitService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        visitService.delete(id);
    }
}
