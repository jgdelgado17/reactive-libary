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
import com.santiagoposada.libraryreactive.utils.ResourceNotFoundException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class ReturnUseCaseTest {

    @MockBean
    private ResourceRepository resourceRepository;

    @SpyBean
    private ReturnUseCase returnUseCase;

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

        String responseMessage = "The resource with id: " + resource.getId() + " was returned successfully";

        Mockito.when(resourceRepository.findById(resourceDTO.getId())).thenReturn(Mono.just(resource));
        Mockito.when(resourceRepository.save(resource)).thenReturn(Mono.just(resource));

        // Act
        Mono<String> result = returnUseCase.apply(resourceDTO.getId());

        // Assert
        StepVerifier.create(result)
                .expectNext(responseMessage)
                .expectComplete()
                .verify();

        Assertions.assertEquals(resourceDTO.getUnitsOwed() - 1, resource.getUnitsOwed());
        Assertions.assertEquals(resourceDTO.getUnitsAvailable() + 1, resource.getUnitsAvailable());
    }

    @Test
    void testApply_IdIsRequired() {
        // Arrange
        String id = null;

        String responseMessage = "Id is required to return a resource";

        Mockito.when(resourceRepository.findById(id)).thenReturn(Mono.empty());

        // Act
        Mono<String> result = returnUseCase.apply(id);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> {
                    Assertions.assertTrue(throwable instanceof ResourceNotFoundException);
                    Assertions.assertEquals(responseMessage, throwable.getMessage());
                    return true;
                })
                .verify();
    }

    @Test
    void testApply_empty() {
        // Arrange
        Mockito.when(resourceRepository.findById("otherId")).thenReturn(Mono.empty());

        // Act
        Mono<String> result = returnUseCase.apply("otherId");

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }
}
