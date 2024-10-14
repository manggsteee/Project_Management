package project.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.management.dto.UserDTO;
import project.management.model.User;
import project.management.repository.UserRepository;
import project.management.request.AddUserRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    @Override
    public UserDTO addUser(AddUserRequest request){
        User user;
        if(userRepository.existsByUsername(request.getUsername())){
            log.error("Username already exists");
            throw new RuntimeException("Username already exists");
        }else
            user = User.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
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
