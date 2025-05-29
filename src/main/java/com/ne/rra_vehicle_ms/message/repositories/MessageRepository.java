package com.ne.rra_vehicle_ms.message.repositories;

import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.message.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByEmployee(Employee employee);
    List<Message> findByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);
    List<Message> findByMonthAndYear(Integer month, Integer year);
    List<Message> findBySent(boolean sent);
    List<Message> findByEmployeeAndSent(Employee employee, boolean sent);
}