package com.santiagoposada.libraryreactive.usecase;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.santiagoposada.libraryreactive.entity.Resource;
import com.santiagoposada.libraryreactive.repository.ResourceRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class CheckAvailabilityUseCaseTest {
    @MockBean
    private ResourceRepository resourceRepository;

    @SpyBean
    private CheckAvailabilityUseCase checkAvailabilityUseCase;

    @Test
    void testApply_ResourceIsAvailable() {
        // Arrange
        String resourceId = "12345";
        Resource resource = new Resource();
        resource.setName("Nombre #1");
        resource.setUnitsAvailable(2);
        resource.setLastBorrow(LocalDate.parse("2022-10-01"));

        String responseMessage = resource.getName() + " is available";

        Mockito.when(resourceRepository.findById(resourceId)).thenReturn(Mono.just(resource));

        // Act
        Mono<String> result = checkAvailabilityUseCase.apply(resourceId);

        // Assert
        StepVerifier.create(result)
                .expectNext(responseMessage)
                .expectComplete()
                .verify();
    }

    @Test
    void testApply_ResourceIsNotAvailable() {
        // Arrange
        String resourceId = "67890";
        Resource resource = new Resource();
        resource.setName("Nombre #2");
        resource.setUnitsAvailable(0);
        resource.setLastBorrow(LocalDate.parse("2022-09-15"));

        String responseMessage = resource.getName() + " is not available, last borrow "
                + resource.getLastBorrow();

        Mockito.when(resourceRepository.findById(resourceId)).thenReturn(Mono.just(resource));

        // Act
        Mono<String> result = checkAvailabilityUseCase.apply(resourceId);

        // Assert
        StepVerifier.create(result)
                .expectNext(responseMessage)
                .expectComplete()
                .verify();

    }
}
