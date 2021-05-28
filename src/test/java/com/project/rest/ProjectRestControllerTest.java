package com.project.rest;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.model.Project;
import com.project.service.ProjectService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin")
public class ProjectRestControllerTest {

    private final String apiPath = "/api/projects";
    private JacksonTester<Project> jacksonTester;
    @MockBean
    private ProjectService mockProjectService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getProjects_oneProjectInDatabase_returnsPageWithOneProject() throws Exception {
        //arrange
        Project project = new Project(1, "TestName", "TestDescription",
                LocalDateTime.now(), LocalDate.of(2020, 6, 7));
        Page<Project> projectsPage = new PageImpl<>(Collections.singletonList(project));
        when(mockProjectService.getProjects(any(Pageable.class))).thenReturn(projectsPage);

        //act
        mockMvc.perform(
            get(apiPath)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
        //assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*]").exists())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].projectId").value(project.getProjectId()))
            .andExpect(jsonPath("$.content[0].name").value(project.getName()));

        verify(mockProjectService, times(1)).getProjects(any(Pageable.class));
        verifyNoMoreInteractions(mockProjectService);
    }

    @Test
    public void getProject_getsProjectId_returnsProjectWithGivenId() throws Exception {
        //arrange
        Project project = new Project(2, "TestName", "TestDescription",
                LocalDateTime.now(), LocalDate.of(2020, 6, 7));
        when(mockProjectService.getProject(project.getProjectId())).thenReturn(Optional.of(project));

        //act
        mockMvc.perform(
            get(apiPath + "/{projectId}", project.getProjectId())
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
        //assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(project.getProjectId()))
            .andExpect(jsonPath("$.name").value(project.getName()));

        verify(mockProjectService, times(1)).getProject(project.getProjectId());
        verifyNoMoreInteractions(mockProjectService);
    }

    @Test
    public void createProject_getsProjectJsonInBody_returnsCreatedStatusAndResourceLocation() throws Exception {
        //arrange
        Project project = new Project(3, "TestName", "TestDescription",
                LocalDateTime.now(), LocalDate.of(2020, 6, 7));
        String projectJson = jacksonTester.write(project).getJson();
        when(mockProjectService.setProject(any(Project.class))).thenReturn(project);

        //act
        mockMvc.perform(
            post(apiPath)
            .content(projectJson)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL))
            .andDo(print())
        //assert
            .andExpect(status().isCreated())
            .andExpect(header().string("location",
                containsString(apiPath + "/" + project.getProjectId())));
    }

    @Test
    public void createProject_getsProjectWithEmptyName_returnsBadRequestStatusAndValidationException() throws Exception {
        //arrange
        Project project = new Project(1, "", "TestDescription",
                LocalDateTime.now(), LocalDate.of(2020, 6, 7));
        String jsonProject = jacksonTester.write(project).getJson();
        when(mockProjectService.setProject(any(Project.class))).thenReturn(project);

        //act
        MvcResult result = mockMvc
            .perform(post(apiPath)
            .content(jsonProject)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andReturn();

        //assert
        verify(mockProjectService, times(0)).setProject(any(Project.class));
        Exception exception = result.getResolvedException();
        assertNotNull(exception);
        assertTrue(exception instanceof MethodArgumentNotValidException);
    }

    @Test
    public void updateProject_getsProjectJsonInBody_returnsOkStatus() throws Exception {
        //arrange
        Project project = new Project(5, "TestName", "TestDescription",
                LocalDateTime.now(), LocalDate.of(2020, 6, 7));
        String jsonProject = jacksonTester.write(project).getJson();
        when(mockProjectService.getProject(project.getProjectId())).thenReturn(Optional.of(project));
        when(mockProjectService.setProject(any(Project.class))).thenReturn(project);

        //act
        mockMvc.perform(
            put(apiPath + "/{projectId}", project.getProjectId())
            .content(jsonProject)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL))
            .andDo(print())
        //assert
            .andExpect(status().isOk());

        verify(mockProjectService, times(1)).getProject(project.getProjectId());
        verify(mockProjectService, times(1)).setProject(any(Project.class));
        verifyNoMoreInteractions(mockProjectService);
    }

    @BeforeEach
    public void before(TestInfo testInfo) {
        System.out.printf("-- METHOD -> %s%n", testInfo.getTestMethod().get().getName());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JacksonTester.initFields(this, mapper);
    }

    @AfterEach
    public void after(TestInfo testInfo) {
        System.out.printf("<- END -- %s%n", testInfo.getTestMethod().get().getName());
    }
}