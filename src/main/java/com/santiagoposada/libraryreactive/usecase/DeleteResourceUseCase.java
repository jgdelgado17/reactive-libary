package com.santiagoposada.libraryreactive.usecase;


import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.santiagoposada.libraryreactive.repository.ResourceRepository;

import reactor.core.publisher.Mono;

@Service
@Validated
public class DeleteResourceUseCase implements Function<String, Mono<Void>> {
    private ResourceRepository resourceRepository;

    public DeleteResourceUseCase(ResourceRepository resourceRepository){
        this.resourceRepository = resourceRepository;
    }

    @Override
    public Mono<Void> apply(String s) {
        return resourceRepository.deleteById(s);
    }
}
