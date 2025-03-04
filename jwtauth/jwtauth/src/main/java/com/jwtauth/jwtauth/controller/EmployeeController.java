package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.dto.EmployeeRequestDTO;
import com.jwtauth.jwtauth.dto.EmployeeResponseDTO;
import com.jwtauth.jwtauth.model.EmployeeEntity;
import com.jwtauth.jwtauth.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Create Employee
    @PostMapping("/register")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@RequestBody EmployeeRequestDTO employeeRequestDTO) {
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setName(employeeRequestDTO.getName());
        employeeEntity.setDepartment(employeeRequestDTO.getDepartment());
        employeeEntity.setSalary(employeeRequestDTO.getSalary());

        EmployeeResponseDTO response = employeeService.createEmployee(employeeEntity);
        return ResponseEntity.ok(response);
    }

    // Get Employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployee(@PathVariable Long id) {
        EmployeeResponseDTO response = employeeService.getEmployee(id);
        return ResponseEntity.ok(response);
    }

    // Search Employees
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponseDTO>> searchEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Double salary) {

        List<EmployeeResponseDTO> response = employeeService.searchEmployees(name, department, salary);
        return ResponseEntity.ok(response);
    }
}
