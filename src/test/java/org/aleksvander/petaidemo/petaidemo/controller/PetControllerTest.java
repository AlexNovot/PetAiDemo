package org.aleksvander.petaidemo.petaidemo.controller;

import org.aleksvander.petaidemo.petaidemo.dto.pet.PetRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetResponseDto;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Species;
import org.aleksvander.petaidemo.petaidemo.exception.GlobalExceptionHandler;
import org.aleksvander.petaidemo.petaidemo.exception.ResourceNotFoundException;
import org.aleksvander.petaidemo.petaidemo.service.PetService;
import org.aleksvander.petaidemo.petaidemo.service.VisitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    @Mock
    private PetService petService;

    @Mock
    private VisitService visitService;

    private MockMvc mockMvc;
    private JsonMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().build();
        PetController controller = new PetController(petService, visitService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getAll_returnsPetsList() throws Exception {
        PetResponseDto dto = new PetResponseDto(10L, "Rex", Species.DOG, "Labrador", LocalDate.of(2020, 1, 1), 1L, 0L);
        when(petService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rex"));
    }

    @Test
    void getById_found_returnsPet() throws Exception {
        PetResponseDto dto = new PetResponseDto(10L, "Rex", Species.DOG, "Labrador", LocalDate.of(2020, 1, 1), 1L, 0L);
        when(petService.getById(10L)).thenReturn(dto);

        mockMvc.perform(get("/api/pets/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.species").value("DOG"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(petService.getById(99L)).thenThrow(new ResourceNotFoundException("Pet not found: 99"));

        mockMvc.perform(get("/api/pets/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getVisits_returnsNestedVisitsList() throws Exception {
        VisitResponseDto visit = new VisitResponseDto(100L, LocalDate.of(2024, 5, 1), "Checkup", "notes", 10L, 0L);
        when(visitService.getByPet(10L)).thenReturn(List.of(visit));

        mockMvc.perform(get("/api/pets/10/visits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diagnosis").value("Checkup"));
    }

    @Test
    void create_validPayload_returns201() throws Exception {
        PetRequestDto request = new PetRequestDto("Rex", Species.DOG, "Labrador", LocalDate.of(2020, 1, 1), 1L, null);
        PetResponseDto response = new PetResponseDto(10L, "Rex", Species.DOG, "Labrador", LocalDate.of(2020, 1, 1), 1L, 0L);
        when(petService.create(any(PetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/pets")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void create_invalidPayload_returns400() throws Exception {
        PetRequestDto invalid = new PetRequestDto("", null, "Labrador", LocalDate.of(2020, 1, 1), null, null);

        mockMvc.perform(post("/api/pets")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.species").exists())
                .andExpect(jsonPath("$.fieldErrors.ownerId").exists());
    }

    @Test
    void update_validPayload_returns200() throws Exception {
        PetRequestDto request = new PetRequestDto("Rex", Species.DOG, "Labrador", LocalDate.of(2020, 1, 1), 1L, null);
        PetResponseDto response = new PetResponseDto(10L, "Rex", Species.DOG, "Labrador", LocalDate.of(2020, 1, 1), 1L, 0L);
        when(petService.update(eq(10L), any(PetRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/pets/10")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.breed").value("Labrador"));
    }

    @Test
    void delete_existingPet_returns204() throws Exception {
        mockMvc.perform(delete("/api/pets/10"))
                .andExpect(status().isNoContent());

        verify(petService).delete(10L);
    }
}
