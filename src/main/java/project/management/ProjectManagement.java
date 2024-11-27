package project.management;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
//@RequiredArgsConstructor
public class ProjectManagement {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        System.setProperty("DATASOURCE_URL", dotenv.get("DATASOURCE_URL"));
        System.setProperty("DATASOURCE_PASSWORD", dotenv.get("DATASOURCE_PASSWORD"));
        System.setProperty("CLIENT_ID", dotenv.get("CLIENT_ID"));
        System.setProperty("CLIENT_SECRET", dotenv.get("CLIENT_SECRET"));
        System.setProperty("UPLOAD_DIR", dotenv.get("UPLOAD_DIR"));
        System.setProperty("SECRET_KEY", dotenv.get("SECRET_KEY"));
        System.setProperty("DOCKER_VOLUME", dotenv.get("DOCKER_VOLUME"));
        System.setProperty("DOCKER_VOLUME_FILE", dotenv.get("DOCKER_VOLUME_FILE"));
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