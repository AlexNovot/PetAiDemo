package org.aleksvander.petaidemo.petaidemo.service;

import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerRequestDto;
import org.aleksvander.petaidemo.petaidemo.dto.owner.OwnerResponseDto;
import org.aleksvander.petaidemo.petaidemo.entity.Owner;
import org.aleksvander.petaidemo.petaidemo.exception.ResourceNotFoundException;
import org.aleksvander.petaidemo.petaidemo.mapper.OwnerMapper;
import org.aleksvander.petaidemo.petaidemo.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private OwnerMapper ownerMapper;

    @InjectMocks
    private OwnerService ownerService;

    private Owner owner;
    private OwnerRequestDto requestDto;
    private OwnerResponseDto responseDto;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1L);
        owner.setFirstName("Ivan");
        owner.setLastName("Petrov");
        owner.setEmail("ivan@example.com");
        owner.setPhone("+79001234567");

        requestDto = new OwnerRequestDto("Ivan", "Petrov", "ivan@example.com", "+79001234567");
        responseDto = new OwnerResponseDto(1L, "Ivan", "Petrov", "ivan@example.com", "+79001234567");
    }

    @Test
    void getAll_returnsMappedOwners() {
        when(ownerRepository.findAll()).thenReturn(List.of(owner));
        when(ownerMapper.toDto(owner)).thenReturn(responseDto);

        List<OwnerResponseDto> result = ownerService.getAll();

        assertThat(result).containsExactly(responseDto);
    }

    @Test
    void getById_found_returnsDto() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ownerMapper.toDto(owner)).thenReturn(responseDto);

        OwnerResponseDto result = ownerService.getById(1L);

        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(ownerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownerService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesEntityAndReturnsDto() {
        when(ownerMapper.toEntity(requestDto)).thenReturn(owner);
        when(ownerRepository.save(owner)).thenReturn(owner);
        when(ownerMapper.toDto(owner)).thenReturn(responseDto);

        OwnerResponseDto result = ownerService.create(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(ownerRepository).save(owner);
    }

    @Test
    void update_found_updatesEntityAndReturnsDto() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ownerMapper.toDto(owner)).thenReturn(responseDto);

        OwnerResponseDto result = ownerService.update(1L, requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(ownerMapper).updateEntity(owner, requestDto);
    }

    @Test
    void update_notFound_throwsAndDoesNotUpdate() {
        when(ownerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownerService.update(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(ownerMapper, never()).updateEntity(any(), any());
    }

    @Test
    void delete_found_deletesById() {
        when(ownerRepository.existsById(1L)).thenReturn(true);

        ownerService.delete(1L);

        verify(ownerRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsAndDoesNotDelete() {
        when(ownerRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> ownerService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(ownerRepository, never()).deleteById(any());
    }

    @Test
    void findOwnerOrThrow_found_returnsEntity() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

        Owner result = ownerService.findOwnerOrThrow(1L);

        assertThat(result).isEqualTo(owner);
    }

    @Test
    void findOwnerOrThrow_notFound_throws() {
        when(ownerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownerService.findOwnerOrThrow(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
