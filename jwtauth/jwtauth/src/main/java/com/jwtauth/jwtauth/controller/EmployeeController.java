package com.jwtauth.jwtauth.controller;

import com.jwtauth.jwtauth.dto.EmployeeRequestDTO;
import com.jwtauth.jwtauth.dto.EmployeeResponseDTO;
import com.jwtauth.jwtauth.entity.EmployeeEntity;
import com.jwtauth.jwtauth.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Create Employee
    @PostMapping("/register")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@RequestBody @Valid EmployeeRequestDTO employeeRequestDTO) {
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

    // Get PDF for Employee
    @GetMapping("/employee/{id}/pdf")
    public ResponseEntity<byte[]> getEmployeePdf(@PathVariable Long id) {
        try {
            // Generate the PDF for the given employee ID
            byte[] pdfContents = employeeService.generateEmployeePdf(id).toByteArray();

            // Set headers for the PDF file download
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=employee_" + id + "_details.pdf");
            headers.add("Content-Type", "application/pdf");

            return new ResponseEntity<>(pdfContents, headers, HttpStatus.OK);
        } catch (IOException | RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
