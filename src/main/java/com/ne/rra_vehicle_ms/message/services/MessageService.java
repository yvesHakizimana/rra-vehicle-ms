package com.ne.rra_vehicle_ms.message.services;

import com.ne.rra_vehicle_ms.employee.dtos.EmployeeResponseDto;
import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employee.services.EmployeeService;
import com.ne.rra_vehicle_ms.message.dtos.MessageRequestDto;
import com.ne.rra_vehicle_ms.message.dtos.MessageResponseDto;
import com.ne.rra_vehicle_ms.message.entities.Message;
import com.ne.rra_vehicle_ms.message.mappers.MessageMapper;
import com.ne.rra_vehicle_ms.message.repositories.MessageRepository;
import com.ne.rra_vehicle_ms.payslip.dtos.PayslipResponseDto;
import com.ne.rra_vehicle_ms.payslip.entities.Payslip;
import com.ne.rra_vehicle_ms.payslip.mappers.PayslipMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final EmployeeService employeeService;
    private final JavaMailSender javaMailSender;
    private final MessageMapper messageMapper;
    private final PayslipMapper payslipMapper;

    @Transactional
    public MessageResponseDto createMessage(MessageRequestDto messageRequestDto) {
        // Get employee entity for database operations
        Employee employee = employeeService.getEmployeeEntityById(messageRequestDto.employeeId());
        
        Message message = messageMapper.toEntity(messageRequestDto);
        message.setEmployee(employee);
        message.setCreatedAt(LocalDateTime.now());
        
        Message savedMessage = messageRepository.save(message);
        return messageMapper.toResponseDto(savedMessage);
    }

    @Transactional(readOnly = true)
    public MessageResponseDto getMessageById(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message with id " + id + " not found"));
        return messageMapper.toResponseDto(message);
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDto> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return messageMapper.toResponseDtoList(messages);
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDto> getMessagesByEmployee(UUID employeeId) {
        Employee employee = employeeService.getEmployeeEntityById(employeeId);
        List<Message> messages = messageRepository.findByEmployee(employee);
        return messageMapper.toResponseDtoList(messages);
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDto> getMessagesByMonthAndYear(Integer month, Integer year) {
        List<Message> messages = messageRepository.findByMonthAndYear(month, year);
        return messageMapper.toResponseDtoList(messages);
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDto> getMessagesBySentStatus(Boolean sent) {
        List<Message> messages = messageRepository.findBySent(sent);
        return messageMapper.toResponseDtoList(messages);
    }

    @Transactional
    public MessageResponseDto updateMessage(UUID id, MessageRequestDto messageRequestDto) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message with id " + id + " not found"));
        
        // Update fields
        message.setMessage(messageRequestDto.message());
        message.setMonth(messageRequestDto.month());
        message.setYear(messageRequestDto.year());
        message.setSent(messageRequestDto.sent());
        
        // Update employee if changed
        if (!message.getEmployee().getId().equals(messageRequestDto.employeeId())) {
            Employee newEmployee = employeeService.getEmployeeEntityById(messageRequestDto.employeeId());
            message.setEmployee(newEmployee);
        }
        
        Message updatedMessage = messageRepository.save(message);
        return messageMapper.toResponseDto(updatedMessage);
    }

    @Transactional
    public void deleteMessage(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message with id " + id + " not found"));
        messageRepository.delete(message);
    }

    @Async
    @Transactional
    public void createAndSendPayslipApprovalMessages(List<Payslip> payslips) {
        for (Payslip payslip : payslips) {
            try {
                // Create message
                String messageContent = String.format(
                        "Dear %s %s, your payslip for %s %d has been approved and paid. " +
                        "Net salary: %s RWF. Please check your account.",
                        payslip.getEmployee().getFirstName(),
                        payslip.getEmployee().getLastName(),
                        Month.of(payslip.getMonth()).name(),
                        payslip.getYear(),
                        payslip.getNetSalary()
                );

                Message message = Message.builder()
                        .employee(payslip.getEmployee())
                        .message(messageContent)
                        .month(payslip.getMonth())
                        .year(payslip.getYear())
                        .createdAt(LocalDateTime.now())
                        .sent(false)
                        .build();

                Message savedMessage = messageRepository.save(message);

                // Send email
                sendPayslipApprovalEmail(payslip);
                
                // Update message as sent
                savedMessage.setSent(true);
                messageRepository.save(savedMessage);

            } catch (Exception e) {
                log.error("Failed to create/send message for payslip {}: {}", payslip.getId(), e.getMessage());
            }
        }
    }

    @Async
    public void sendPayslipApprovalEmail(Payslip payslip) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(payslip.getEmployee().getEmail());
            mailMessage.setSubject("Payslip Approved - " + Month.of(payslip.getMonth()).name() + " " + payslip.getYear());
            mailMessage.setText(String.format(
                    "Dear %s %s,\n\n" +
                    "Your payslip for %s %d has been approved and processed.\n\n" +
                    "Payslip Details:\n" +
                    "- Gross Salary: %s RWF\n" +
                    "- Net Salary: %s RWF\n" +
                    "- Month/Year: %s %d\n\n" +
                    "Please check your bank account for the payment.\n\n" +
                    "Best regards,\n" +
                    "HR Department",
                    payslip.getEmployee().getFirstName(),
                    payslip.getEmployee().getLastName(),
                    Month.of(payslip.getMonth()).name(),
                    payslip.getYear(),
                    payslip.getGrossSalary(),
                    payslip.getNetSalary(),
                    Month.of(payslip.getMonth()).name(),
                    payslip.getYear()
            ));

            javaMailSender.send(mailMessage);
            log.info("Payslip approval email sent to: {}", payslip.getEmployee().getEmail());
        } catch (Exception e) {
            log.error("Failed to send payslip approval email to {}: {}", 
                    payslip.getEmployee().getEmail(), e.getMessage());
        }
    }
}
