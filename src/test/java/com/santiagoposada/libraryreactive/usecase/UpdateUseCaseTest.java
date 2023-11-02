package com.santiagoposada.libraryreactive.usecase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.santiagoposada.libraryreactive.dto.ResourceDTO;
import com.santiagoposada.libraryreactive.entity.Resource;
import com.santiagoposada.libraryreactive.repository.ResourceRepository;
import com.santiagoposada.libraryreactive.utils.ResourceNotFoundException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;

@SpringBootTest
public class UpdateUseCaseTest {

    @MockBean
    private ResourceRepository resourceRepository;

    @SpyBean
    private UpdateUseCase updateUseCase;

    @Test
    void testApply() {
        // Arrange
        Resource resource = new Resource();

        resource.setId("1233435ff");
        resource.setName("Nombre #1");
        resource.setType("Tipo #1");
        resource.setCategory("Area tematica #1");
        resource.setUnitsAvailable(10);
        resource.setUnitsOwed(5);
        resource.setLastBorrow(LocalDate.parse("2020-01-10"));

        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setId(resource.getId());
        resourceDTO.setName(resource.getName());
        resourceDTO.setType(resource.getType());
        resourceDTO.setCategory(resource.getCategory());
        resourceDTO.setUnitsAvailable(resource.getUnitsAvailable());
        resourceDTO.setUnitsOwed(resource.getUnitsOwed());
        resourceDTO.setLastBorrow(resource.getLastBorrow());

        Mockito.when(resourceRepository.save(any())).thenReturn(Mono.just(resource));

        // Act
        Mono<ResourceDTO> result = updateUseCase.apply(resourceDTO);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(resourceCurrent -> {
                    Assertions.assertEquals(resourceDTO, resourceCurrent);
                    Assertions.assertEquals(resourceDTO.getId(), resourceCurrent.getId());
                    return resourceCurrent.getId().equals(resourceDTO.getId());
                })
                .verifyComplete();
    }

    @Test
    void testApply_IdIsRequired() {
        // Arrange
        Resource resource = new Resource();

        resource.setId("1233435ff");
        // resource.setName("Nombre #1");
        resource.setType("Tipo #1");
        resource.setCategory("Area tematica #1");
        resource.setUnitsAvailable(10);
        resource.setUnitsOwed(5);
        resource.setLastBorrow(LocalDate.parse("2020-01-10"));

        ResourceDTO resourceDTO = new ResourceDTO();
        // resourceDTO.setId(resource.getId());
        resourceDTO.setName(resource.getName());
        resourceDTO.setType(resource.getType());
        resourceDTO.setCategory(resource.getCategory());
        resourceDTO.setUnitsAvailable(resource.getUnitsAvailable());
        resourceDTO.setUnitsOwed(resource.getUnitsOwed());
        resourceDTO.setLastBorrow(resource.getLastBorrow());

        Mockito.when(resourceRepository.save(any())).thenReturn(Mono.just(resource));

        // Act
        Mono<ResourceDTO> result = updateUseCase.apply(resourceDTO);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> {
                    Assertions.assertTrue(throwable instanceof ResourceNotFoundException);
                    Assertions.assertEquals("Id is required to update a resource", throwable.getMessage());
                    return true;
                })
                .verify();
    }
}
