package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.model.StudentEntity;
import com.jwtauth.jwtauth.repository.StudentRepository;
import com.jwtauth.jwtauth.service.GenericCrudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final GenericCrudService<StudentEntity, Long> service;

    public StudentController(StudentRepository studentRepository) {
        this.service = new GenericCrudService<>(studentRepository);
    }

    @PostMapping
    public ResponseEntity<StudentEntity> create(@RequestBody StudentEntity student) {
        StudentEntity createdStudent = service.save(student);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudentEntity>> getAll() {
        List<StudentEntity> students = service.findAll();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentEntity> getById(@PathVariable Long id) {
        Optional<StudentEntity> student = service.findById(id);
        if (student.isPresent()) {
            return new ResponseEntity<>(student.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update an existing student entity
    @PutMapping("/{id}")
    public ResponseEntity<StudentEntity> update(@PathVariable Long id, @RequestBody StudentEntity updatedStudent) {
        try {
            StudentEntity updated = service.update(id, updatedStudent);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
