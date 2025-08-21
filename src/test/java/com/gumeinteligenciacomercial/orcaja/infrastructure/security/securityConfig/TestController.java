package com.gumeinteligenciacomercial.orcaja.infrastructure.security.securityConfig;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/hello")
    public String hello() {
        return "Hello API";
    }

    @GetMapping("/login")
    public String login() {
        return "Login";
    }

    @GetMapping("/oauth2/test")
    public String oauthTest() {
        return "OAuth2";
    }
}
