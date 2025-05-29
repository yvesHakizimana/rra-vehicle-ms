package com.ne.rra_vehicle_ms.message.controllers;

import com.ne.rra_vehicle_ms.message.dtos.MessageRequestDto;
import com.ne.rra_vehicle_ms.message.dtos.MessageResponseDto;
import com.ne.rra_vehicle_ms.message.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message Management", description = "APIs for managing employee messages")
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "Create a new message", description = "Creates a new message for an employee. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message created successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MessageResponseDto> createMessage(@Valid @RequestBody MessageRequestDto messageRequestDto) {
        return new ResponseEntity<>(messageService.createMessage(messageRequestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get message by ID", description = "Retrieves a message by its UUID. Requires ADMIN or MANAGER role.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MessageResponseDto> getMessageById(@PathVariable UUID id) {
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @Operation(summary = "Get all messages", description = "Retrieves all messages. Requires ADMIN or MANAGER role.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<MessageResponseDto>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @Operation(summary = "Get messages by employee", description = "Retrieves all messages for a specific employee. Requires ADMIN or MANAGER role.")
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<MessageResponseDto>> getMessagesByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(messageService.getMessagesByEmployee(employeeId));
    }

    @Operation(summary = "Get messages by month and year", description = "Retrieves all messages for a specific month and year. Requires ADMIN or MANAGER role.")
    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<MessageResponseDto>> getMessagesByMonthAndYear(
            @PathVariable Integer month, @PathVariable Integer year) {
        return ResponseEntity.ok(messageService.getMessagesByMonthAndYear(month, year));
    }

    @Operation(summary = "Get messages by sent status", description = "Retrieves messages by their sent status. Requires ADMIN or MANAGER role.")
    @GetMapping("/sent/{sent}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<MessageResponseDto>> getMessagesBySentStatus(@PathVariable Boolean sent) {
        return ResponseEntity.ok(messageService.getMessagesBySentStatus(sent));
    }

    @Operation(summary = "Update message", description = "Updates an existing message. Requires ADMIN or MANAGER role.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MessageResponseDto> updateMessage(
            @PathVariable UUID id, @Valid @RequestBody MessageRequestDto messageRequestDto) {
        return ResponseEntity.ok(messageService.updateMessage(id, messageRequestDto));
    }

    @Operation(summary = "Delete message", description = "Deletes a message. Requires ADMIN role.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}