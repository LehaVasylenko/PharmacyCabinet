package com.orders.cabinet.controller.health;

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
@Tag(name = "Health Controller", description = "Checks if the application is responsive")
public class HealthController {

    /**
     * Health check endpoint.
     *
     * <p>Returns "pong" if the application is running correctly.</p>
     *
     * @return a response entity with the status and message "pong"
     */
    @GetMapping("${main.module.ping}")
    @Operation(summary = "Check application",
            description = "Allowed for all users. Returns pong if everything OK",
            tags = {"Health"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application OK",
                    content = @Content(
                            examples = @ExampleObject(value = "pong")
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request. Captain Obvious is in touch",
                    content = @Content(
                            examples = @ExampleObject(value = "  ")
                    )),
            @ApiResponse(responseCode = "500", description = "Usually, a 500 answer doesn't mean anything good. And it's the same story",
                    content = @Content(
                            examples = @ExampleObject(value = "  ")
                    ))
    })
    public ResponseEntity<String> pong() {
        return ResponseEntity.status(200).body("pong");
    }
}
