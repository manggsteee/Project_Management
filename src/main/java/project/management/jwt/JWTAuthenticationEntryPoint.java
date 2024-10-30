package project.management.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import project.management.project_enum.ExceptionEnum;
import project.management.response.ApiResponse;

import java.io.IOException;

public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ExceptionEnum exceptionEnum = ExceptionEnum.UNAUTHENTICATED;
        response.setStatus(exceptionEnum.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().print(
                new ObjectMapper().writeValueAsString(
                ApiResponse.builder()
                .code(exceptionEnum.getCode())
                .message(exceptionEnum.getMessage())
                .build()));
        response.flushBuffer();
    }
}
