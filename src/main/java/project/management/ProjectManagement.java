package project.management;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
//@RequiredArgsConstructor
public class ProjectManagement {

    public static void main(String[] args) {
        SpringApplication.run(ProjectManagement.class, args);
    }


//    private final UserService userService;
//    @Override
//    public void run(String... args) throws Exception {
//        AddUserRequest user = new AddUserRequest(
//                "phucnh",
//                "phuc@123",
//                "Nguyen Hoang Phuc",
//                UserEnum.USER
//                );
//        AddUserRequest user2 = new AddUserRequest(
//                "andrew",
//                "andrew@123",
//                "Nicolas Andrew",
//                UserEnum.USER
//        );
//        System.out.println(userService.addUser(user));
//        System.out.println(userService.addUser(user2));
//    }
}