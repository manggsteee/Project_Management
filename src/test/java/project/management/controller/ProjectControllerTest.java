package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import project.management.dto.MemberRoleDTO;
import project.management.dto.ProjectDTO;
import project.management.project_enum.MemberRoleEnum;
import project.management.project_enum.ProjectStatus;
import project.management.request.MemberRolesRequest;
import project.management.request.ProjectRequest;
import project.management.response.ApiResponse;
import project.management.service.ProjectService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    private ApiResponse apiResponse;
    private ProjectRequest projectRequest;
    private ProjectDTO projectDTO;

    @BeforeEach
    void init() {
        projectRequest = ProjectRequest.builder()
                .title("New Project")
                .description("This is a description of the new project.")
                .startDate(LocalDateTime.of(2024, 12, 26, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 26, 0, 0))
                .memberRoles(List.of(MemberRolesRequest.builder()
                                .userName("phucnh")
                                .role(MemberRoleEnum.MANAGER)
                                .build(),
                        MemberRolesRequest.builder()
                                .userName("andrew")
                                .role(MemberRoleEnum.MEMBER)
                                .build()))
                .build();
        projectDTO = ProjectDTO.builder()
                .id(1L)
                .name("New Project")
                .description("This is a description of the new project.")
                .startDate(LocalDateTime.of(2024, 12, 26, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 26, 0, 0))
                .status(ProjectStatus.NOT_STARTED)
                .projectAttachments(null)
                .memberRoles(List.of(MemberRoleDTO.builder()
                                .userName("phucnh")
                                .role(MemberRoleEnum.MANAGER)
                                .build(),
                        MemberRoleDTO.builder()
                                .userName("andrew")
                                .role(MemberRoleEnum.MEMBER)
                                .build()))
                .build();
        apiResponse = ApiResponse.builder()
                .code(1000)
                .message("Create Project Successfully")
                .data(projectDTO)
                .build();
    }

    @Test
    void createProjectSuccessfully() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Mockito.when(projectService.createProject(ArgumentMatchers.any()
                , ArgumentMatchers.any())).thenReturn(projectDTO);
        String requestBody = objectMapper.writeValueAsString(projectRequest);
        MockMultipartFile projectInformation = new
                MockMultipartFile("project_information",
                "project_information.json",
                MediaType.APPLICATION_JSON_VALUE, requestBody.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/project/project/create")
                .file("description_files", new byte[0])
                .file(projectInformation)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Project created successfully"));
    }
}
