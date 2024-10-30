package project.management.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import project.management.dto.response.MemberRoleDTO;
import project.management.dto.response.ProjectDTO;
import project.management.exception.ApplicationException;
import project.management.project_enum.ExceptionEnum;
import project.management.project_enum.MemberRoleEnum;
import project.management.project_enum.ProjectStatus;
import project.management.dto.request.MemberRolesRequest;
import project.management.dto.request.ProjectRequest;
import project.management.response.ApiResponse;
import project.management.service.ProjectService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProjectService projectService;
    private ApiResponse apiResponse;
    private ProjectRequest projectRequest;
    private ProjectDTO projectDTO;
    private MockMultipartFile projectInformation;
    private ProjectRequest updateRequest;
    private ProjectDTO updatedProjectDTO;
    private MockMultipartFile updateInformation;

    @BeforeEach
    void init() throws JsonProcessingException {
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
        objectMapper.registerModule(new JavaTimeModule());
        String requestBody = objectMapper.writeValueAsString(projectRequest);
        projectInformation = new
                MockMultipartFile("project_information",
                "project_information.json",
                MediaType.APPLICATION_JSON_VALUE, requestBody.getBytes());
        updateRequest = ProjectRequest.builder()
                .title("Update Project")
                .description("This is a description of the update project.")
                .startDate(LocalDateTime.of(2024, 10, 1, 0, 0))
                .endDate(LocalDateTime.of(2024, 12, 31, 0, 0))
                .status(ProjectStatus.IN_PROGRESS)
                .memberRoles(List.of(
                        MemberRolesRequest.builder()
                                .userName("phucnh")
                                .role(MemberRoleEnum.MANAGER)
                                .build(),
                        MemberRolesRequest.builder()
                                .userName("andrew")
                                .role(MemberRoleEnum.MEMBER)
                                .build()
                ))
                .build();
        updatedProjectDTO = ProjectDTO.builder()
                .id(1L)
                .name("Update Project")
                .description("This is a description of the update project.")
                .startDate(LocalDateTime.of(2024, 10, 1, 0, 0))
                .endDate(LocalDateTime.of(2024, 12, 31, 0, 0))
                .status(ProjectStatus.IN_PROGRESS)
                .projectAttachments(null)
                .memberRoles(List.of(
                        MemberRoleDTO.builder()
                                .userName("phucnh")
                                .role(MemberRoleEnum.MANAGER)
                                .build(),
                        MemberRoleDTO.builder()
                                .userName("andrew")
                                .role(MemberRoleEnum.MEMBER)
                                .build()
                ))
                .build();
        String updateRequestBody = objectMapper.writeValueAsString(updateRequest);
        updateInformation = new MockMultipartFile("update_informations",
                "update_informations.json",
                MediaType.APPLICATION_JSON_VALUE, updateRequestBody.getBytes());
    }

    @Test
    void createProjectSuccessfully() throws Exception {
        Mockito.when(projectService.createProject(ArgumentMatchers.any()
                , ArgumentMatchers.any())).thenReturn(projectDTO);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/project_management/v1/project/create")
                        .file("description_files", new byte[0])
                        .file(projectInformation)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Create Project Successfully"));
    }

    @Test
    void createProjectThrowsGenericException() throws Exception {
        Mockito.when(projectService.createProject(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/project_management/v1/project/create")
                        .file("description_files", new byte[0])
                        .file(projectInformation)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(9999))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Application has some problems")); // Kiểm tra thông điệp
    }
    @Test
    void updateProjectSuccessfully() throws Exception {
        Long projectId = 1L;

        Mockito.when(projectService.updateProject(ArgumentMatchers.eq(projectId), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(updatedProjectDTO);

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,"/project_management/v1/project/" + projectId + "/update")
                        .file(updateInformation)
                        .file("update_attachments", new byte[0]) // Giả lập tệp đính kèm nếu có
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Update Project Successfully"));
    }

    @Test
    void updateProjectThrowsNotFoundException() throws Exception {
        Long projectId = 1L;
        Mockito.when(projectService.updateProject(ArgumentMatchers.eq(projectId), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenThrow(new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,
                                "/project_management/v1/project/" + projectId + "/update")
                        .file("update_attachments", new byte[0])
                        .file(updateInformation)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1100))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Project not found"));
    }

    @Test
    void deleteProjectSuccessfully() throws Exception {
        Long projectId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/project_management/v1/project/" + projectId + "/delete"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Delete Project Successfully"));

        Mockito.verify(projectService, Mockito.times(1)).deleteProject(projectId);
    }

    @Test
    void deleteProjectThrowsNotFoundException() throws Exception {
        Long projectId = 1L;
        Mockito.doThrow(new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND))
                .when(projectService).deleteProject(projectId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/project_management/v1/project/" + projectId + "/delete"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1100))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Project not found"));
    }

    @Test
    void getProjectByIdSuccessfully() throws Exception {
        Long projectId = 1L;
        String userName = "testUser";

        Mockito.when(projectService.getProjectById(userName, projectId))
                .thenReturn(projectDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/project_management/v1/project/get/by/id")
                        .param("username", userName)
                        .param("id", String.valueOf(projectId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Project with id 1 is found."));
    }

    @Test
    void getProjectByIdThrowsNotFoundException() throws Exception {
        Long projectId = 1L;
        String userName = "testUser";

        Mockito.when(projectService.getProjectById(userName, projectId))
                .thenThrow(new ApplicationException(ExceptionEnum.PROJECT_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/project_management/v1/project/get/by/id")
                        .param("username", userName)
                        .param("id", String.valueOf(projectId)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1100))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Project not found"));
    }
}
