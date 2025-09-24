package com.example.keycloak_demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keycloak")
public class DemoController {

    @GetMapping
    @PreAuthorize("hasRole('ROLE_client_admin')")
    public String hello() {
        return "Hello Spring And Keycloak";
    }

    @GetMapping("/2")
    public String hello2() {
        return "Hello Spring And Keycloak 2";
    }
}
