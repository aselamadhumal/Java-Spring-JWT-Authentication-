package com.jwtauth.jwtauth.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class GenericCrudService<T, ID> {

    private final JpaRepository<T, ID> repository;

    public GenericCrudService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    // Simplified update method
    public T update(ID id, T updatedEntity) {
        Optional<T> existingEntityOpt = repository.findById(id);
        if (existingEntityOpt.isPresent()) {
            T existingEntity = existingEntityOpt.get();

            updateFields(existingEntity, updatedEntity);

            return repository.save(existingEntity);
        }
        throw new RuntimeException("Entity not found with ID: " + id);
    }

    private void updateFields(T existingEntity, T updatedEntity) {
        if (updatedEntity != null) {
            for (var field : updatedEntity.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(updatedEntity);
                    if (value != null) {
                        // Update field in existing entity
                        field.set(existingEntity, value);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error updating fields", e);
                }
            }
        }
    }
}
