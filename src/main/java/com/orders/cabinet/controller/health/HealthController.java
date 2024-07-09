package com.orders.cabinet.controller.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("${main.module.ping}")
    public ResponseEntity<String> pong() {
        return ResponseEntity.status(200).body("pong");
    }
}
