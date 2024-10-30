package project.management.service;

import project.management.dto.response.UserDTO;
import project.management.dto.request.AddUserRequest;

public interface UserServiceInterface {
    UserDTO addUser(AddUserRequest user);
}
