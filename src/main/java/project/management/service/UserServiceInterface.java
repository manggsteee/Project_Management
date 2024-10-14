package project.management.service;

import project.management.dto.UserDTO;
import project.management.request.AddUserRequest;

public interface UserServiceInterface {
    UserDTO addUser(AddUserRequest user);
}
