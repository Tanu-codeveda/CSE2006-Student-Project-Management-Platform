package com.vityarthi.cove.dto;

import com.vityarthi.cove.model.Role;

public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String roleDescription;
    private String dashboardUrl;
    private String registrationOrEmpId;
    private String message;

    public AuthResponse() {}

    public AuthResponse(Long id, String name, String email, Role role, String roleDescription, String dashboardUrl, String registrationOrEmpId, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.roleDescription = roleDescription;
        this.dashboardUrl = dashboardUrl;
        this.registrationOrEmpId = registrationOrEmpId;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }

    public String getRegistrationOrEmpId() {
        return registrationOrEmpId;
    }

    public void setRegistrationOrEmpId(String registrationOrEmpId) {
        this.registrationOrEmpId = registrationOrEmpId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
