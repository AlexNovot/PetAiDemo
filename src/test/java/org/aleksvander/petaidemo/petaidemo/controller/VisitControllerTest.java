package org.aleksvander.petaidemo.petaidemo.controller;

import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.visit.VisitResponseDto;
import org.aleksvander.petaidemo.petaidemo.exception.GlobalExceptionHandler;
import org.aleksvander.petaidemo.petaidemo.exception.ResourceNotFoundException;
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
class VisitControllerTest {

    @Mock
    private VisitService visitService;

    private MockMvc mockMvc;
    private JsonMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().build();
        VisitController controller = new VisitController(visitService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getAll_returnsVisitsList() throws Exception {
        VisitResponseDto dto = new VisitResponseDto(100L, LocalDate.of(2024, 5, 1), "Checkup", "notes", 10L, 0L);
        when(visitService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/visits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diagnosis").value("Checkup"));
    }

    @Test
    void getById_found_returnsVisit() throws Exception {
        VisitResponseDto dto = new VisitResponseDto(100L, LocalDate.of(2024, 5, 1), "Checkup", "notes", 10L, 0L);
        when(visitService.getById(100L)).thenReturn(dto);

        mockMvc.perform(get("/api/visits/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petId").value(10));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(visitService.getById(999L)).thenThrow(new ResourceNotFoundException("Visit not found: 999"));

        mockMvc.perform(get("/api/visits/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validPayload_returns201() throws Exception {
        VisitRequestDto request = new VisitRequestDto(LocalDate.of(2024, 5, 1), "Checkup", "notes", 10L, null);
        VisitResponseDto response = new VisitResponseDto(100L, LocalDate.of(2024, 5, 1), "Checkup", "notes", 10L, 0L);
        when(visitService.create(any(VisitRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/visits")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void create_invalidPayload_returns400() throws Exception {
        VisitRequestDto invalid = new VisitRequestDto(null, "Checkup", "notes", null, null);

        mockMvc.perform(post("/api/visits")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.visitDate").exists())
                .andExpect(jsonPath("$.fieldErrors.petId").exists());
    }

    @Test
    void update_validPayload_returns200() throws Exception {
        VisitRequestDto request = new VisitRequestDto(LocalDate.of(2024, 5, 1), "Checkup", "notes", 10L, null);
        VisitResponseDto response = new VisitResponseDto(100L, LocalDate.of(2024, 5, 1), "Checkup", "notes", 10L, 0L);
        when(visitService.update(eq(100L), any(VisitRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/visits/100")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Checkup"));
    }

    @Test
    void delete_existingVisit_returns204() throws Exception {
        mockMvc.perform(delete("/api/visits/100"))
                .andExpect(status().isNoContent());

        verify(visitService).delete(100L);
    }
}
