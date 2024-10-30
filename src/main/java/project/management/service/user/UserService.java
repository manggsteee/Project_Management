package project.management.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.management.dto.request.AddUserRequest;
import project.management.dto.response.UserDTO;
import project.management.exception.ApplicationException;
import project.management.model.User;
import project.management.project_enum.ExceptionEnum;
import project.management.repository.UserRepository;
import project.management.service.UserServiceInterface;


@Service
@Slf4j
public class UserService implements UserServiceInterface, UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(UserInfoDetails::new)
                .orElseThrow(() -> new ApplicationException(
                        ExceptionEnum.USER_NOT_FOUND));
    }

    @Override
    public UserDTO addUser(AddUserRequest request) {
        User user;
        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("Username already exists");
            throw new ApplicationException(ExceptionEnum.USER_EXISTED);
        } else
            user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .fullName(request.getFullName())
                    .userEnum(request.getUserEnum())
                    .build();

        userRepository.save(user);
        return UserDTO.builder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .build();
    }
}
