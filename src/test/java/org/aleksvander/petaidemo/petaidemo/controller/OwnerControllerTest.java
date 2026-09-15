package org.aleksvander.petaidemo.petaidemo.controller;

import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerResponseDto;
import org.aleksvander.petaidemo.petaidemo.dto.pet.PetResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Species;
import org.aleksvander.petaidemo.petaidemo.exception.GlobalExceptionHandler;
import org.aleksvander.petaidemo.petaidemo.exception.ResourceNotFoundException;
import org.aleksvander.petaidemo.petaidemo.service.OwnerService;
import org.aleksvander.petaidemo.petaidemo.service.PetService;
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
class OwnerControllerTest {

    @Mock
    private OwnerService ownerService;

    @Mock
    private PetService petService;

    private MockMvc mockMvc;
    private JsonMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().build();
        OwnerController controller = new OwnerController(ownerService, petService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getAll_returnsOwnersList() throws Exception {
        OwnerResponseDto dto = new OwnerResponseDto(1L, "Ivan", "Petrov", "ivan@example.com", "+79001234567");
        when(ownerService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/owners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Ivan"));
    }

    @Test
    void getById_found_returnsOwner() throws Exception {
        OwnerResponseDto dto = new OwnerResponseDto(1L, "Ivan", "Petrov", "ivan@example.com", "+79001234567");
        when(ownerService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/owners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ivan@example.com"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(ownerService.getById(99L)).thenThrow(new ResourceNotFoundException("Owner not found: 99"));

        mockMvc.perform(get("/api/owners/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Owner not found: 99"));
    }

    @Test
    void getPets_returnsNestedPetsList() throws Exception {
        PetResponseDto pet = new PetResponseDto(10L, "Rex", Species.DOG, "Labrador", LocalDate.of(2020, 1, 1), 1L);
        when(petService.getByOwner(1L)).thenReturn(List.of(pet));

        mockMvc.perform(get("/api/owners/1/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rex"));
    }

    @Test
    void create_validPayload_returns201() throws Exception {
        OwnerRequestDto request = new OwnerRequestDto("Ivan", "Petrov", "ivan@example.com", "+79001234567");
        OwnerResponseDto response = new OwnerResponseDto(1L, "Ivan", "Petrov", "ivan@example.com", "+79001234567");
        when(ownerService.create(any(OwnerRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/owners")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_invalidPayload_returns400() throws Exception {
        OwnerRequestDto invalid = new OwnerRequestDto("", "Petrov", "not-an-email", null);

        mockMvc.perform(post("/api/owners")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.firstName").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void update_validPayload_returns200() throws Exception {
        OwnerRequestDto request = new OwnerRequestDto("Ivan", "Petrov", "ivan@example.com", "+79001234567");
        OwnerResponseDto response = new OwnerResponseDto(1L, "Ivan", "Petrov", "ivan@example.com", "+79001234567");
        when(ownerService.update(eq(1L), any(OwnerRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/owners/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Petrov"));
    }

    @Test
    void delete_existingOwner_returns204() throws Exception {
        mockMvc.perform(delete("/api/owners/1"))
                .andExpect(status().isNoContent());

        verify(ownerService).delete(1L);
    }
}
