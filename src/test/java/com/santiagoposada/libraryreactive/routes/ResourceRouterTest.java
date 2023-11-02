package com.santiagoposada.libraryreactive.routes;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.santiagoposada.libraryreactive.dto.ResourceDTO;
import com.santiagoposada.libraryreactive.entity.Resource;
import com.santiagoposada.libraryreactive.usecase.BorrowResourceUseCase;
import com.santiagoposada.libraryreactive.usecase.CheckAvailabilityUseCase;
import com.santiagoposada.libraryreactive.usecase.CreateResourceUseCase;
import com.santiagoposada.libraryreactive.usecase.DeleteResourceUseCase;
import com.santiagoposada.libraryreactive.usecase.GetAllUseCase;
import com.santiagoposada.libraryreactive.usecase.GetByCategoryUseCase;
import com.santiagoposada.libraryreactive.usecase.GetByTypeUseCase;
import com.santiagoposada.libraryreactive.usecase.GetResourceByIdUseCase;
import com.santiagoposada.libraryreactive.usecase.ReturnUseCase;
import com.santiagoposada.libraryreactive.usecase.UpdateUseCase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class ResourceRouterTest {

        private WebTestClient webTestClient;

        @MockBean
        private BorrowResourceUseCase borrowResourceUseCase;

        @MockBean
        private CheckAvailabilityUseCase checkAvailabilityUseCase;

        @MockBean
        private CreateResourceUseCase createResourceUseCase;

        @MockBean
        private DeleteResourceUseCase deleteResourceUseCase;

        @MockBean
        private GetAllUseCase getAllUseCase;

        @MockBean
        private GetByCategoryUseCase getByCategoryUseCase;

        @MockBean
        private GetByTypeUseCase getByTypeUseCase;

        @MockBean
        private GetResourceByIdUseCase getResourceByIdUseCase;

        @MockBean
        private ReturnUseCase returnUseCase;

        @MockBean
        private UpdateUseCase updateUseCase;

        @Test
        void testBorrowResourceRoute() {
                // Arrange
                Resource resource = new Resource();

                resource.setId("1233435ff");
                resource.setName("Nombre #1");
                resource.setType("Tipo #1");
                resource.setCategory("Area tematica #1");
                resource.setUnitsAvailable(0);
                resource.setUnitsOwed(5);
                resource.setLastBorrow(LocalDate.parse("2020-01-10"));

                String responseMessage = "The resource " + resource.getName() + " has been borrowed, there are "
                                + (resource.getUnitsAvailable() - 1) + " units available";

                Mockito.when(borrowResourceUseCase.apply(resource.getId())).thenReturn(Mono.just(responseMessage));

                webTestClient = WebTestClient
                                .bindToRouterFunction(new ResourceRouter().borrowResourceRoute(borrowResourceUseCase))
                                .build();

                // Act
                webTestClient.put()
                                .uri("/borrow/{id}", resource.getId())
                                .exchange()
                                // Assert
                                .expectStatus().isOk()
                                .expectBody(String.class)
                                .value(result -> {
                                        StepVerifier.create(Mono.just(result))
                                                        .expectNext(responseMessage)
                                                        .expectComplete()
                                                        .verify();
                                });
        }

        @Test
        void testCheckForAvailabilityRoute() {
                // Arrange
                String resourceId = "67890";
                Resource resource = new Resource();
                resource.setName("Nombre #2");
                resource.setUnitsAvailable(0);
                resource.setLastBorrow(LocalDate.parse("2022-09-15"));

                String responseMessage = resource.getName() + " is available";

                Mockito.when(checkAvailabilityUseCase.apply(resourceId)).thenReturn(Mono.just(responseMessage));

                webTestClient = WebTestClient
                                .bindToRouterFunction(new ResourceRouter()
                                                .checkForAvailabilityRoute(checkAvailabilityUseCase))
                                .build();

                // Act
                webTestClient.get()
                                .uri("/availability/{id}", resourceId)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(String.class)
                                .value(result -> {
                                        // Assert
                                        StepVerifier.create(Mono.just(result))
                                                        .expectNext(responseMessage)
                                                        .expectComplete()
                                                        .verify();
                                });
        }

        @Test
        void testCheckForNotAvailabilityRoute() {
                // Arrange
                String resourceId = "67890";
                Resource resource = new Resource();
                resource.setName("Nombre #2");
                resource.setUnitsAvailable(0);
                resource.setLastBorrow(LocalDate.parse("2022-09-15"));

                String responseMessage = resource.getName() + " is not available, last borrow "
                                + resource.getLastBorrow();

                Mockito.when(checkAvailabilityUseCase.apply(resourceId)).thenReturn(Mono.just(responseMessage));

                webTestClient = WebTestClient
                                .bindToRouterFunction(new ResourceRouter()
                                                .checkForAvailabilityRoute(checkAvailabilityUseCase))
                                .build();

                // Act
                webTestClient.get()
                                .uri("/availability/{id}", resourceId)
                                .exchange()
                                // Assert
                                .expectStatus().isOk()
                                .expectBody(String.class)
                                .value(result -> {
                                        StepVerifier.create(Mono.just(result))
                                                        .expectNext(responseMessage)
                                                        .expectComplete()
                                                        .verify();
                                });
        }

        @Test
        void testCreateResourceRoute() {
                // Arrange
                ResourceDTO resourceDTO = new ResourceDTO();
                resourceDTO.setId("1233435ff");
                resourceDTO.setName("Nombre #1");
                resourceDTO.setType("Tipo #1");
                resourceDTO.setCategory("Area tematica #1");
                resourceDTO.setUnitsAvailable(10);
                resourceDTO.setUnitsOwed(5);
                resourceDTO.setLastBorrow(LocalDate.parse("2020-01-10"));

                Mockito.when(createResourceUseCase.apply(resourceDTO)).thenReturn(Mono.just(resourceDTO));

                webTestClient = WebTestClient
                                .bindToRouterFunction(new ResourceRouter().createResourceRoute(createResourceUseCase))
                                .build();

                // Act
                webTestClient.post()
                                .uri("/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(resourceDTO), ResourceDTO.class)
                                .exchange()
                                // Assert
                                .expectStatus().isOk()
                                .expectBody(ResourceDTO.class)
                                .value(result -> {
                                        StepVerifier.create(Mono.just(result))
                                                        .expectNextMatches(resourceCurrent -> {
                                                                Assertions.assertEquals(resourceDTO, resourceCurrent);
                                                                Assertions.assertEquals(resourceDTO.getId(),
                                                                                resourceCurrent.getId());
                                                                return resourceCurrent.getId()
                                                                                .equals(resourceDTO.getId());
                                                        })
                                                        .expectComplete()
                                                        .verify();

                                        // StepVerifier.create(Mono.just(result))
                                        // .expectNext(resourceDTO)
                                        // .expectComplete()
                                        // .verify();
                                });
        }

        @Test
        void testDeleteResourceToute() {
                // Arrange
                String resourceId = "12345";

                Mockito.when(deleteResourceUseCase.apply(resourceId))
                                .thenReturn(Mono.empty());

                webTestClient = WebTestClient
                                .bindToRouterFunction(new ResourceRouter().deleteResourceToute(deleteResourceUseCase))
                                .build();

                // Act
                webTestClient.delete()
                                .uri("/delete/{id}", resourceId)
                                .exchange()
                                .expectStatus().isAccepted()
                                .expectBody(Void.class)
                                .value(result -> {
                                        // Assert
                                        StepVerifier.create(Mono.empty())
                                                        .expectComplete()
                                                        .verify();
                                });
        }

        @Test
        void testGetAllRouter() {
                // Arrange
                ResourceDTO resource1 = new ResourceDTO();

                resource1.setId("1233435ff");
                resource1.setName("Nombre #1");
                resource1.setType("Tipo #1");
                resource1.setCategory("Area tematica #1");
                resource1.setUnitsAvailable(10);
                resource1.setUnitsOwed(5);
                resource1.setLastBorrow(LocalDate.parse("2020-01-10"));

                ResourceDTO resource2 = new ResourceDTO();

                resource2.setId("1233435ff");
                resource2.setName("Nombre #1");
                resource2.setType("Tipo #1");
                resource2.setCategory("Area tematica #1");
                resource2.setUnitsAvailable(10);
                resource2.setUnitsOwed(5);
                resource2.setLastBorrow(LocalDate.parse("2020-01-10"));

                Flux<ResourceDTO> resourceFlux = Flux.just(resource1, resource2);

                webTestClient = WebTestClient
                                .bindToRouterFunction(new ResourceRouter().getAllRouter(getAllUseCase))
                                .build();

                Mockito.when(getAllUseCase.get()).thenReturn(resourceFlux);

                // Act
                webTestClient.get()
                                .uri("/resources")
                                .exchange()
                                // Assert
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBodyList(ResourceDTO.class)
                                .value(result -> {
                                        StepVerifier.create(Flux.fromIterable(result))
                                                        .expectNext(resource1, resource2)
                                                        .expectComplete()
                                                        .verify();
                                });
        }

        @Test
        void testGetByCategory() {
                // Arrange
                String category = "Books";

                ResourceDTO resource1 = new ResourceDTO();

                resource1.setId("1233435ff");
                resource1.setName("Nombre #1");
                resource1.setType("Tipo #1");
                resource1.setCategory(category);
                resource1.setUnitsAvailable(10);
                resource1.setUnitsOwed(5);
                resource1.setLastBorrow(LocalDate.parse("2020-01-10"));

                ResourceDTO resource2 = new ResourceDTO();

                resource2.setId("1233435ff");
                resource2.setName("Nombre #1");
                resource2.setType("Tipo #1");
                resource2.setCategory(category);
                resource2.setUnitsAvailable(10);
                resource2.setUnitsOwed(5);
                resource2.setLastBorrow(LocalDate.parse("2020-01-10"));

                Flux<ResourceDTO> resourceFlux = Flux.just(resource1, resource2);

                Mockito.when(getByCategoryUseCase.apply(category)).thenReturn(resourceFlux);

                webTestClient = WebTestClient
                                .bindToRouterFunction(new ResourceRouter().getByCategory(getByCategoryUseCase))
                                .build();

                // Act
                webTestClient.get()
                                .uri("/getByCategory/{category}", category)
                                .exchange()
                                // Assert
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBodyList(ResourceDTO.class)
                                .value(result -> {
                                        StepVerifier.create(Flux.fromIterable(result))
                                                        .expectNext(resource1, resource2)
                                                        .expectComplete()
                                                        .verify();
                                });
        }

        @Test
        void testGetByTypeRoute() {
                // Arrange
                String type = "Video Games";

                ResourceDTO resource1 = new ResourceDTO();

                resource1.setId("1233435ff");
                resource1.setName("Nombre #1");
                resource1.setType(type);
                resource1.setCategory("Area tematica #1");
                resource1.setUnitsAvailable(10);
                resource1.setUnitsOwed(5);
                resource1.setLastBorrow(LocalDate.parse("2020-01-10"));

                ResourceDTO resource2 = new ResourceDTO();

                resource2.setId("1233435ff");
                resource2.setName("Nombre #1");
                resource2.setType(type);
                resource2.setCategory("Area tematica #1");
                resource2.setUnitsAvailable(10);
                resource2.setUnitsOwed(5);
                resource2.setLastBorrow(LocalDate.parse("2020-01-10"));

                Flux<ResourceDTO> resourceFlux = Flux.just(resource1, resource2);

                webTestClient = WebTestClient
                                .bindToRouterFunction(new ResourceRouter().getByTypeRoute(getByTypeUseCase))
                                .build();

                Mockito.when(getByTypeUseCase.apply(type)).thenReturn(resourceFlux);

                // Act
                webTestClient.get()
                                .uri("/getByType/{type}", type)
                                .exchange()
                                // Assert
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBodyList(ResourceDTO.class)
                                .value(result -> {
                                        StepVerifier.create(Flux.fromIterable(result))
                                                        .expectNext(resource1, resource2)
                                                        .expectComplete()
                                                        .verify();
                                });
        }

        @Test
        void testGetResourceById() {
                // Arrange
                String resourceId = "12345";
                ResourceDTO resource = new ResourceDTO();

                resource.setId(resourceId);
                resource.setName("Nombre #1");
                resource.setType("Tipo #1");
                resource.setCategory("Area tematica #1");
                resource.setUnitsAvailable(10);
                resource.setUnitsOwed(5);
                resource.setLastBorrow(LocalDate.parse("2020-01-10"));

                webTestClient = WebTestClient
                                .bindToRouterFunction(new ResourceRouter().getResourceById(getResourceByIdUseCase))
                                .build();

                Mockito.when(getResourceByIdUseCase.apply(resourceId)).thenReturn(Mono.just(resource));

                // Act
                webTestClient.get()
                                .uri("/resource/{id}", resourceId)
                                .exchange()
                                // Assert
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(ResourceDTO.class)
                                .value(result -> {
                                        StepVerifier.create(Mono.just(result))
                                                        .expectNext(resource)
                                                        .expectComplete()
                                                        .verify();
                                });
        }

        @Test
        void testReturnRoute() {
                // Arrange
                String resourceId = "12345";

                ResourceDTO resource = new ResourceDTO();

                resource.setId(resourceId);
                resource.setName("Nombre #1");
                resource.setType("Tipo #1");
                resource.setCategory("Area tematica #1");
                resource.setUnitsAvailable(10);
                resource.setUnitsOwed(5);
                resource.setLastBorrow(LocalDate.parse("2020-01-10"));

                String responseMessage = "The resource with id: " + resource.getId() + " was returned successfully";

                Mockito.when(returnUseCase.apply(resourceId)).thenReturn(Mono.just(responseMessage));

                webTestClient = WebTestClient
                                .bindToRouterFunction(new ResourceRouter().returnRoute(returnUseCase))
                                .build();

                // Act
                webTestClient.put()
                                .uri("/return/{id}", resourceId)
                                .exchange()
                                // Assert
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(String.class)
                                .value(result -> {
                                        StepVerifier.create(Mono.just(result))
                                                        .expectNext(responseMessage)
                                                        .expectComplete()
                                                        .verify();
                                });
        }

        @Test
        void testUpdateResourceRoute() {
                // Arrange
                ResourceDTO resourceDTO = new ResourceDTO();
                resourceDTO.setId("1233435ff");
                resourceDTO.setName("Nombre #1");
                resourceDTO.setType("Tipo #1");
                resourceDTO.setCategory("Area tematica #1");
                resourceDTO.setUnitsAvailable(10);
                resourceDTO.setUnitsOwed(5);
                resourceDTO.setLastBorrow(LocalDate.parse("2020-01-10"));

                Mockito.when(updateUseCase.apply(resourceDTO)).thenReturn(Mono.just(resourceDTO));

                webTestClient = WebTestClient
                                .bindToRouterFunction(new ResourceRouter().updateResourceRoute(updateUseCase))
                                .build();

                // Act
                webTestClient.put()
                                .uri("/update")
                                .body(Mono.just(resourceDTO), ResourceDTO.class)
                                .exchange()
                                // Assert
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(ResourceDTO.class)
                                .value(result -> {
                                        StepVerifier.create(Mono.just(result))
                                                        .expectNextMatches(updatedResource -> {
                                                                return updatedResource.getId()
                                                                                .equals(resourceDTO.getId())
                                                                                && updatedResource.getName().equals(
                                                                                                resourceDTO.getName())
                                                                                && updatedResource.getType().equals(
                                                                                                resourceDTO.getType())
                                                                                && updatedResource.getCategory().equals(
                                                                                                resourceDTO.getCategory())
                                                                                && updatedResource
                                                                                                .getUnitsAvailable() == resourceDTO
                                                                                                                .getUnitsAvailable()
                                                                                && updatedResource
                                                                                                .getUnitsOwed() == resourceDTO
                                                                                                                .getUnitsOwed();
                                                        })
                                                        .expectComplete()
                                                        .verify();
                                });
        }
}
