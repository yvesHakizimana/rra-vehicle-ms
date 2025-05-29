package com.ne.rra_vehicle_ms.message.services;

import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employee.services.EmployeeService;
import com.ne.rra_vehicle_ms.message.entities.Message;
import com.ne.rra_vehicle_ms.message.repositories.MessageRepository;
import com.ne.rra_vehicle_ms.payslip.entities.Payslip;
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
    private final JavaMailSender mailSender;
    
    private static final String INSTITUTION_NAME = "Rwanda Government";

    @Transactional
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public Message getMessageById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByEmployee(UUID employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        return messageRepository.findByEmployee(employee);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByEmployeeAndMonthAndYear(UUID employeeId, Integer month, Integer year) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        return messageRepository.findByEmployeeAndMonthAndYear(employee, month, year);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByMonthAndYear(Integer month, Integer year) {
        return messageRepository.findByMonthAndYear(month, year);
    }

    @Transactional(readOnly = true)
    public List<Message> getUnsentMessages() {
        return messageRepository.findBySent(false);
    }

    @Transactional
    public Message createPayslipApprovalMessage(Payslip payslip) {
        Employee employee = payslip.getEmployee();
        String monthName = Month.of(payslip.getMonth()).name();
        
        String messageText = String.format(
                "Dear %s, your salary for %s/%d from %s amounting to %s has been credited to your account %s successfully.",
                employee.getFirstName(),
                monthName,
                payslip.getYear(),
                INSTITUTION_NAME,
                payslip.getNetSalary().toString(),
                employee.getCode()
        );
        
        Message message = Message.builder()
                .employee(employee)
                .message(messageText)
                .month(payslip.getMonth())
                .year(payslip.getYear())
                .createdAt(LocalDateTime.now())
                .sent(false)
                .build();
        
        return messageRepository.save(message);
    }
    
    @Transactional
    @Async
    public void sendMessage(UUID messageId) {
        Message message = getMessageById(messageId);
        Employee employee = message.getEmployee();
        
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(employee.getEmail());
            mailMessage.setSubject("Salary Payment Notification");
            mailMessage.setText(message.getMessage());
            
            mailSender.send(mailMessage);
            
            message.setSent(true);
            messageRepository.save(message);
            
            log.info("Email sent to {} for {}/{} payslip", employee.getEmail(), message.getMonth(), message.getYear());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", employee.getEmail(), e.getMessage());
        }
    }
    
    @Transactional
    public void sendAllUnsentMessages() {
        List<Message> unsentMessages = getUnsentMessages();
        for (Message message : unsentMessages) {
            sendMessage(message.getId());
        }
    }
    
    @Transactional
    public void createAndSendPayslipApprovalMessages(List<Payslip> approvedPayslips) {
        for (Payslip payslip : approvedPayslips) {
            Message message = createPayslipApprovalMessage(payslip);
            sendMessage(message.getId());
        }
    }
}