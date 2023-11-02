package com.santiagoposada.libraryreactive.usecase;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.santiagoposada.libraryreactive.repository.ResourceRepository;

import reactor.core.publisher.Mono;

@SpringBootTest
public class DeleteResourceUseCaseTest {

    @MockBean
    private ResourceRepository resourceRepository;

    @SpyBean
    private DeleteResourceUseCase deleteResourceUseCase;

    /* @BeforeEach
    void setUp() {
        deleteResourceUseCase = new DeleteResourceUseCase(resourceRepository);
    } */

    @Test
    void testApply() {
        // Arrange
        String resourceId = "12345";
        
        Mockito.when(resourceRepository.deleteById(resourceId)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = deleteResourceUseCase.apply(resourceId);

        // Assert
        result.subscribe();
        Mockito.verify(resourceRepository, Mockito.times(1)).deleteById(resourceId);
    }
}
