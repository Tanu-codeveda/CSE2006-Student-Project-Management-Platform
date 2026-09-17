package com.vityarthi.cove.service;

import com.vityarthi.cove.dto.AuthRequest;
import com.vityarthi.cove.dto.AuthResponse;
import com.vityarthi.cove.exception.UnauthorizedActionException;
import com.vityarthi.cove.exception.UserNotFoundException;
import com.vityarthi.cove.model.*;
import com.vityarthi.cove.repository.AdministratorRepository;
import com.vityarthi.cove.repository.FacultyRepository;
import com.vityarthi.cove.repository.StudentRepository;
import com.vityarthi.cove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication and User Registration Service.
 * Demonstrates:
 * - CSE2006 Unit 2: Polymorphism, dynamic dispatch, subtype instantiation.
 * - CSE2006 Unit 3: Exception handling.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    @Autowired
    public AuthService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       FacultyRepository facultyRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email '" + request.getEmail() + "' already exists.");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.STUDENT;
        User newUser;
        String regOrEmpId = "N/A";

        // Polymorphic entity instantiation based on role (CSE2006 Unit 2)
        switch (role) {
            case STUDENT:
                String regNo = request.getRegistrationNumber() != null ? request.getRegistrationNumber() : "21BCE" + System.currentTimeMillis() % 10000;
                String dept = request.getDepartment() != null ? request.getDepartment() : "Computer Science & Engineering";
                int year = request.getYearOfStudy() != null ? request.getYearOfStudy() : 2;
                Student student = new Student(request.getName(), request.getEmail(), request.getPassword(), regNo, dept, year, request.getBio());
                newUser = studentRepository.save(student);
                regOrEmpId = regNo;
                break;

            case FACULTY:
                String empId = request.getEmployeeId() != null ? request.getEmployeeId() : "FAC" + System.currentTimeMillis() % 10000;
                String designation = request.getDesignation() != null ? request.getDesignation() : "Assistant Professor";
                String facultyDept = request.getDepartment() != null ? request.getDepartment() : "School of Computing Science";
                Faculty faculty = new Faculty(request.getName(), request.getEmail(), request.getPassword(), empId, facultyDept, designation);
                newUser = facultyRepository.save(faculty);
                regOrEmpId = empId;
                break;

            case ADMINISTRATOR:
                Administrator admin = new Administrator(request.getName(), request.getEmail(), request.getPassword(), "ADM" + System.currentTimeMillis() % 1000, true);
                newUser = userRepository.save(admin);
                regOrEmpId = admin.getAdminCode();
                break;

            default:
                throw new IllegalArgumentException("Unsupported user role: " + role);
        }

        return new AuthResponse(
                newUser.getId(),
                newUser.getName(),
                newUser.getEmail(),
                newUser.getRole(),
                newUser.getRole().getDescription(),
                newUser.getDashboardUrl(),
                regOrEmpId,
                "Registration successful."
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Invalid email or password."));

        if (!user.getPassword().equals(password)) {
            throw new UnauthorizedActionException("Invalid email or password.");
        }

        String regOrEmpId = "N/A";
        if (user instanceof Student s) {
            regOrEmpId = s.getRegistrationNumber();
        } else if (user instanceof Faculty f) {
            regOrEmpId = f.getEmployeeId();
        } else if (user instanceof Administrator a) {
            regOrEmpId = a.getAdminCode();
        }

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getRole().getDescription(),
                user.getDashboardUrl(),
                regOrEmpId,
                "Login successful."
        );
    }
}
