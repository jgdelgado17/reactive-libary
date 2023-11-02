package com.santiagoposada.libraryreactive.usecase;

import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.santiagoposada.libraryreactive.repository.ResourceRepository;

import reactor.core.publisher.Mono;

@Service
@Validated
public class CheckAvailabilityUseCase implements Function<String, Mono<String>> {

    private ResourceRepository resourceRepository;

    public CheckAvailabilityUseCase(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public Mono<String> apply(String id) {
        return resourceRepository.findById(id).flatMap(
                resource -> {
                    if (resource.getUnitsAvailable() > 0) {
                        return Mono.just(resource.getName() + " is available");
                    }
                    return Mono.just(resource.getName() + " is not available, last borrow "
                            + resource.getLastBorrow());
                });
    }
}
