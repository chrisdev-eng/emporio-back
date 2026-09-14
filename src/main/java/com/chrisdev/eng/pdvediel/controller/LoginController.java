package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.controller.dto.LoginRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.LoginResponseDTO;
import com.chrisdev.eng.pdvediel.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/login")
@CrossOrigin(
        origins = "http://localhost:4200",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowedHeaders = "*",
        allowCredentials = "true"
)
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = loginService.autenticar(request);
        return ResponseEntity.ok(response);
    }
}