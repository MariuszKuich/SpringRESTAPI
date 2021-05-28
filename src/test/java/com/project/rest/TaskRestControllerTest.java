package com.project.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Matchers.anyInt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.model.Task;
import com.project.service.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
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
import java.util.Collections;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin")
public class TaskRestControllerTest {

	private final String apiPath = "/api/tasks";
	private JacksonTester<Task> jacksonTester;
	@MockBean
	private TaskService mockTaskService;
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getTasks_oneTaskInDatabase_returnsPageWithOneTask() throws Exception {
		//arrange
		Task task = new Task(1, "testName", 1, "testDescription");
		Page<Task> taskPage = new PageImpl<Task>(Collections.singletonList(task));
		when(mockTaskService.getTasks(any(Pageable.class))).thenReturn(taskPage);

		//act
		mockMvc.perform(
			get(apiPath)
			.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
		//assert
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[*]").exists())
			.andExpect(jsonPath("$.content.length()").value(1))
			.andExpect(jsonPath("$.content[0].taskId").value(task.getTaskId()))
			.andExpect(jsonPath("$.content[0].name").value(task.getName()));

		verify(mockTaskService, times(1)).getTasks(any(Pageable.class));
		verifyNoMoreInteractions(mockTaskService);
	}

	@Test
	public void getTask_getsTaskId_returnsTaskWithGivenId() throws Exception {
		//arrange
		Task task = new Task(1, "testName", 1, "testDescription");
		when(mockTaskService.getTask(task.getTaskId())).thenReturn(Optional.of(task));

		//act
		mockMvc.perform(
			get(apiPath + "/{taskId}", task.getTaskId())
			.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
		//assert
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.taskId").value(task.getTaskId()))
			.andExpect(jsonPath("$.description").value(task.getDescription()));

		verify(mockTaskService, times(1)).getTask(any(Integer.class));
		verifyNoMoreInteractions(mockTaskService);
	}

	@Test
	public void createTask_getsTaskInBody_returnsCreatedStatusAndPathToResource() throws Exception {
		//arrange
		Task task = new Task(1, "testName", 1, "testDescription");
		String taskJson = jacksonTester.write(task).getJson();
		when(mockTaskService.setTask(any(Task.class))).thenReturn(task);

		//act
		mockMvc.perform(
			post(apiPath)
			.content(taskJson)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.ALL))
			.andDo(print())
		//assert
			.andExpect(status().isCreated())
			.andExpect(header().string("location",
					containsString(apiPath + "/" + task.getTaskId())));
	}

	@Test
	public void updateTask_getsTaskInBody_returnsOkStatus() throws Exception {
		//arrange
		Task task = new Task(1, "testName", 1, "testDescription");
		String taskJson = jacksonTester.write(task).getJson();
		when(mockTaskService.getTask(task.getTaskId())).thenReturn(Optional.of(task));
		when(mockTaskService.setTask(any(Task.class))).thenReturn(task);

		//act
		mockMvc.perform(
			put(apiPath + "/{taskId}", task.getTaskId())
			.content(taskJson)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.ALL))
			.andDo(print())
		//assert
			.andExpect(status().isOk());

		verify(mockTaskService, times(1)).getTask(any(Integer.class));
		verify(mockTaskService, times(1)).setTask(any(Task.class));
		verifyNoMoreInteractions(mockTaskService);
	}

	@Test
	public void deleteTask_getsTaskId_returnsOkStatus() throws Exception {
		//arrange
		when(mockTaskService.getTask(any(Integer.class))).thenReturn(Optional.of(new Task()));
		Mockito.doNothing().when(mockTaskService).deleteTask(any(Integer.class));

		//act
		mockMvc.perform(
			delete(apiPath + "/{taskId}", anyInt())
			.contentType(MediaType.APPLICATION_JSON))
		//assert
			.andExpect(status().isOk());

		verify(mockTaskService, times(1)).getTask(any(Integer.class));
		verify(mockTaskService, times(1)).deleteTask(any(Integer.class));
		verifyNoMoreInteractions(mockTaskService);
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
