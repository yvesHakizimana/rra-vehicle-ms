package com.ne.rra_vehicle_ms.message.controllers;

import com.ne.rra_vehicle_ms.message.entities.Message;
import com.ne.rra_vehicle_ms.message.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Message> getMessageById(@PathVariable UUID id) {
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<Message>> getMessagesByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(messageService.getMessagesByEmployee(employeeId));
    }

    @GetMapping("/employee/{employeeId}/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<Message>> getMessagesByEmployeeAndMonthAndYear(
            @PathVariable UUID employeeId, @PathVariable Integer month, @PathVariable Integer year) {
        return ResponseEntity.ok(messageService.getMessagesByEmployeeAndMonthAndYear(employeeId, month, year));
    }

    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Message>> getMessagesByMonthAndYear(
            @PathVariable Integer month, @PathVariable Integer year) {
        return ResponseEntity.ok(messageService.getMessagesByMonthAndYear(month, year));
    }

    @GetMapping("/unsent")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Message>> getUnsentMessages() {
        return ResponseEntity.ok(messageService.getUnsentMessages());
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> sendMessage(@PathVariable UUID id) {
        messageService.sendMessage(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-all-unsent")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> sendAllUnsentMessages() {
        messageService.sendAllUnsentMessages();
        return ResponseEntity.ok().build();
    }
}