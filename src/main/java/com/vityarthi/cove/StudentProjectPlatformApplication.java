package com.vityarthi.cove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Entry Point for the Student Project & Team Management Platform.
 * CSE2006 – Programming in Java | VIT Bhopal University
 * 
 * Demonstrates:
 * - Java classes, objects, and entry point execution (Unit 1 & 2).
 * - Component scanning and layered architecture.
 */
@SpringBootApplication
@EnableScheduling
public class StudentProjectPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentProjectPlatformApplication.class, args);
    }
}
