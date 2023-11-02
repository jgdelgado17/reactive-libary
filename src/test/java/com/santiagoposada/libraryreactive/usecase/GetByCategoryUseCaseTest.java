package com.santiagoposada.libraryreactive.usecase;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.santiagoposada.libraryreactive.dto.ResourceDTO;
import com.santiagoposada.libraryreactive.entity.Resource;
import com.santiagoposada.libraryreactive.repository.ResourceRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
public class GetByCategoryUseCaseTest {
    
    @MockBean
    private ResourceRepository resourceRepository;

    @SpyBean
    private GetByCategoryUseCase getByCategoryUseCase;

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

        Mockito.when(resourceRepository.findAllByCategory(resourceDTO.getCategory())).thenReturn(Flux.just(resource));

        // Act
        Flux<ResourceDTO> result = getByCategoryUseCase.apply(resourceDTO.getCategory());

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
    void testApply_empty() {
        // Arrange
        Mockito.when(resourceRepository.findAllByCategory("otherCategory")).thenReturn(Flux.empty());

        // Act
        Flux<ResourceDTO> result = getByCategoryUseCase.apply("otherCategory");

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }
}
