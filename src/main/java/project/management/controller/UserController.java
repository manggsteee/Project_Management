package project.management.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import project.management.dto.request.AddUserRequest;
import project.management.dto.request.AuthenticateRequest;
import project.management.exception.ApplicationException;
import project.management.project_enum.ExceptionEnum;
import project.management.response.ApiResponse;
import project.management.jwt.JWTService;
import project.management.service.user.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/auth")
public class UserController {
    private final UserService userService;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome";
    }

    @PostMapping("/registry")
    public ApiResponse register(
            @RequestBody AddUserRequest addUserRequest) {
        return ApiResponse.builder()
                .message("Create User Successfully")
                .data(userService.addUser(addUserRequest))
                .build();
    }

    @PostMapping("login")
    public ApiResponse login(
            @RequestBody AuthenticateRequest authenticateRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticateRequest.getUsername(),
                        authenticateRequest.getPassword()));
                if (authentication.isAuthenticated()) {
                    return ApiResponse.builder()
                            .message("Login Successfully")
                            .data(
                                    jwtService.generateToken(
                                            authenticateRequest
                                                    .getUsername()))
                            .build();
                }else
                    throw new ApplicationException(ExceptionEnum.INVALID_USER);
    }
}
