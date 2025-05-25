package com.ne.rra_vehicle_ms.users;


import com.ne.rra_vehicle_ms.auth.dtos.RegisterRequestDto;
import com.ne.rra_vehicle_ms.commons.exceptions.BadRequestException;
import com.ne.rra_vehicle_ms.users.dtos.UserResponseDto;
import com.ne.rra_vehicle_ms.users.mappers.UserMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // Will continue to run at the startup.
    @PostConstruct
    public void seedAdmin(){
        var adminEmail = "admin@example.com";
        var adminNationalId = "1200780123456083";
        var adminPhoneNumber = "0783520475";
        if(!userRepository.existsByEmailOrPhoneNumberOrNationalId(adminEmail, adminPhoneNumber, adminNationalId)){
            var admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail(adminEmail);
            admin.setNationalId(adminNationalId);
            admin.setPhoneNumber(adminPhoneNumber);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
        } else {
            log.info("Admin user already exists");
        }
    }

    public UserResponseDto createUser(RegisterRequestDto user) {
        if(userRepository.existsByEmailOrPhoneNumberOrNationalId(user.email(), user.phoneNumber(), user.nationalId()))
            throw new BadRequestException("User with this email or nationalId or  phone number already exists.");

        var newUser = userMapper.toEntity(user);
        newUser.setPassword(passwordEncoder.encode(user.password()));
        newUser.setRole(Role.STANDARD);
        newUser.setEnabled(false);
        log.info("user is here, {}", newUser);
        userRepository.save(newUser);
        return userMapper.toResponseDto(newUser);
    }

    public void changeUserPassword(String userEmail, String newPassword){
        var user = findByEmail(userEmail);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void activateUserAccount(String userEmail){
        var user = findByEmail(userEmail);
        user.setEnabled(true);
        userRepository.save(user);
    }


    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("User with that email not found."));
    }
}
