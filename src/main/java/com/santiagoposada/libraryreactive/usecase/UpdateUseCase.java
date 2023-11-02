package com.santiagoposada.libraryreactive.usecase;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.santiagoposada.libraryreactive.dto.ResourceDTO;
import com.santiagoposada.libraryreactive.mapper.ResourceMapper;
import com.santiagoposada.libraryreactive.repository.ResourceRepository;
import com.santiagoposada.libraryreactive.utils.ResourceNotFoundException;

import reactor.core.publisher.Mono;

@Service
@Validated
public class UpdateUseCase implements CreateResource {

    private ResourceMapper resourceMapper;
    private ResourceRepository resourceRepository;

    public UpdateUseCase(ResourceMapper resourceMapper, ResourceRepository resourceRepository) {
        this.resourceMapper = resourceMapper;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public Mono<ResourceDTO> apply(ResourceDTO resourceDTO) {
        // Objects.requireNonNull(resourceDTO.getId());
        if (resourceDTO.getId() == null) {
            return Mono.error(new ResourceNotFoundException("Id is required to update a resource"));
        }
        return resourceRepository.save(resourceMapper.fromResourceDTOtoEntity().apply(resourceDTO))
                .map(resource -> resourceMapper.fromResourceEntityToDTO().apply(resource));
    }
}
