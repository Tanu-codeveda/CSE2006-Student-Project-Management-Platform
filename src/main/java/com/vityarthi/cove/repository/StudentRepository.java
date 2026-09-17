package com.vityarthi.cove.repository;

import com.vityarthi.cove.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegistrationNumber(String registrationNumber);
    Optional<Student> findByEmail(String email);
    boolean existsByRegistrationNumber(String registrationNumber);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.skills sk LEFT JOIN FETCH sk.skill WHERE s.active = true")
    List<Student> findAllActiveWithSkills();

    @Query("SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.registrationNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> searchStudents(@Param("query") String query);
}
