package com.orders.cabinet.controller.health;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Controller for health check operations.
 *
 * <p>This controller provides an endpoint to check if the application is responsive.</p>
 *
 * @version 1.0
 * @since 2024-07-19
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 */
@RestController
public class HealthController {

    /**
     * Health check endpoint.
     *
     * <p>Returns "pong" if the application is running correctly.</p>
     *
     * @return a response entity with the status and message "pong"
     */
    @Hidden
    @GetMapping("${main.module.ping}")
    public ResponseEntity<String> pong() {
        return ResponseEntity.status(200).body("pong");
    }
}
