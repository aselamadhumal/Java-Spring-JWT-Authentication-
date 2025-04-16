package com.jwtauth.jwtauth.service;

import com.itextpdf.text.DocumentException;
import com.jwtauth.jwtauth.dto.EmployeePdfDTO;
import com.jwtauth.jwtauth.dto.EmployeeResponseDTO;
import com.jwtauth.jwtauth.entity.EmployeeEntity;
import com.jwtauth.jwtauth.repository.EmployeeRepository;
import com.jwtauth.jwtauth.utils.PDFGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EntityManager entityManager;
    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    public EmployeeResponseDTO createEmployee(EmployeeEntity employeeEntity) {
        EmployeeEntity savedEmployee = employeeRepository.save(employeeEntity);
        return new EmployeeResponseDTO(
                savedEmployee.getId(),
                savedEmployee.getName(),
                savedEmployee.getDepartment(),
                savedEmployee.getSalary()
        );
    }


    public EmployeeResponseDTO getEmployee(Long id) {
        EmployeeEntity employeeEntity = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return new EmployeeResponseDTO(
                employeeEntity.getId(),
                employeeEntity.getName(),
                employeeEntity.getDepartment(),
                employeeEntity.getSalary()
        );
    }


    public List<EmployeeResponseDTO> searchEmployees(String name, String department, Double salary) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();//build the query
        CriteriaQuery<EmployeeEntity> query = cb.createQuery(EmployeeEntity.class);//Represents the query itself(EmployeeEntity)
        Root<EmployeeEntity> root = query.from(EmployeeEntity.class);//Acts as the query’s starting point

        List<Predicate> predicates = new ArrayList<>();//define the filter and apply to the query

        //Filtering by Name
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(root.get("name"), "%" + name + "%"));
        }

        //Filtering by Department
        if (department != null && !department.isEmpty()) {
            predicates.add(cb.equal(root.get("department"), department));
        }

        //Filtering by salary(in this given salary 125 then display the 125 above details)
        if (salary != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), salary));
        }


        //apply to the all combination to the query
        query.where(cb.and(predicates.toArray(new Predicate[0])));


        //Executes the query and fetches the matching EmployeeEntity results.
        List<EmployeeEntity> employees = entityManager.createQuery(query).getResultList();


        List<EmployeeResponseDTO> responseDTOs = new ArrayList<>();
        for (EmployeeEntity employee : employees) {
            responseDTOs.add(new EmployeeResponseDTO(
                    employee.getId(),
                    employee.getName(),
                    employee.getDepartment(),
                    employee.getSalary()
            ));
        }


        return responseDTOs;
    }

    public EmployeePdfDTO convertToEmployeePdfDTO(EmployeeEntity employeeEntity) {
        return new EmployeePdfDTO(
                employeeEntity.getId(),
                employeeEntity.getName(),
                employeeEntity.getDepartment(),
                employeeEntity.getSalary()
        );
    }
    // Generate PDF for Employee
    public ByteArrayOutputStream generateEmployeePdf(Long employeeId) throws IOException {
        try {
            // Fetch employee by ID
            EmployeeEntity employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            // Convert EmployeeEntity to EmployeePdfDTO
            EmployeePdfDTO employeePdfDTO = convertToEmployeePdfDTO(employee);

            return PDFGenerator.generateEmployeePdf(employeePdfDTO);
        } catch (IOException | DocumentException e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }






}
