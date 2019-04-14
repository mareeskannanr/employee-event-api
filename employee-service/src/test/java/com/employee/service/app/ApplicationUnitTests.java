package com.employee.service.app;

import com.employee.service.app.models.Employee;
import com.employee.service.app.repositories.DepartmentRepository;
import com.employee.service.app.repositories.EmployeeRepository;
import com.employee.service.app.services.KafkaProducerService;
import com.employee.service.app.utils.AppConstants;
import com.employee.service.app.utils.TestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(secure = false)
public class ApplicationUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentRepository departmentRepository;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Value(AppConstants.USER_NAME)
    private String userName;

    @Value(AppConstants.PASSWORD)
    private String password;

    @Value(AppConstants.USER)
    private String roleUser;

    /***** Create Department Test Cases *****/
    @Test
    public void departmentWithEmptyName() throws Exception {
        MvcResult result = mockMvc.perform(post(AppConstants.API + AppConstants.DEPARTMENT)
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        assertEquals(AppConstants.NAME_REQUIRED, response.get(0));
    }

    @Test
    public void departmentWithDuplicateName() throws Exception {
        doReturn(TestUtils.createDepartment()).when(departmentRepository).findByName(any(String.class));

        MvcResult result = mockMvc.perform(post(AppConstants.API + AppConstants.DEPARTMENT)
                .content("{\"name\":\"Sales\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        assertEquals(AppConstants.DEPARTMENT_EXISTS, response.get(0));
    }

    @Test
    public void departmentWithUniqueName() throws Exception {
        doReturn(null).when(departmentRepository).findByName(any(String.class));

        MvcResult result = mockMvc.perform(post(AppConstants.API + AppConstants.DEPARTMENT)
                .content("{\"name\":\"Sales\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
    }

    /***** Create Employee Test *****/
    @Test
    public void employeeWithUnauthorized() throws Exception {
        MvcResult result = mockMvc.perform(post(AppConstants.API + AppConstants.EMPLOYEE)
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }

    @Test
    public void employeeWithEmptyObject() throws Exception {
        MvcResult result = mockMvc.perform(post(AppConstants.API + AppConstants.EMPLOYEE)
                .content("{}")
                .with(user(userName).password(password).roles(roleUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        assertEquals(AppConstants.EMAIL_REQUIRED, response.get(0));
    }

    @Test
    public void employeeWithInvalidEmail() throws Exception {
        MvcResult result = mockMvc.perform(post(AppConstants.API + AppConstants.EMPLOYEE)
                .content("{\"email\": \"aaaaa\"}")
                .with(user(userName).password(password).roles(roleUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        assertEquals(AppConstants.EMAIL_INVALID, response.get(0));
    }

    @Test
    public void employeeWithInvalidObject() throws Exception {
        //Date Value contains string
        MvcResult result = mockMvc.perform(post(AppConstants.API + AppConstants.EMPLOYEE)
                .content("{\"birthday\": \"ssss\"}")
                .with(user(userName).password(password).roles(roleUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        assertEquals(AppConstants.INVALID_POST_MSG, response.get(0));
    }

    @Test
    public void employeeWithExistEmail() throws Exception {
        doReturn(TestUtils.createEmployees(1).get(0)).when(employeeRepository).findByEmail(any(String.class));

        MvcResult result = mockMvc.perform(post(AppConstants.API + AppConstants.EMPLOYEE)
                .content("{\"email\": \"aaa@gmail.com\"}")
                .with(user(userName).password(password).roles(roleUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        assertEquals(AppConstants.EMAIL_EXISTS, response.get(0));
    }

    @Test
    public void employeeWithNotExistDepartment() throws Exception {
        doReturn(null).when(employeeRepository).findByEmail(any(String.class));
        doReturn(Optional.empty()).when(departmentRepository).findById(any(Long.class));

        MvcResult result = mockMvc.perform(post(AppConstants.API + AppConstants.EMPLOYEE)
                .content("{\"email\": \"aaa@gmail.com\", \"departmentId\": 12}")
                .with(user(userName).password(password).roles(roleUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        assertEquals(AppConstants.DEPARTMENT_NOT_EXISTS, response.get(0));
    }

    @Test
    public void employeeWithValidObject() throws Exception {
        doReturn(null).when(employeeRepository).findByEmail(any(String.class));
        doReturn(Optional.of(TestUtils.createDepartment())).when(departmentRepository).findById(any(Long.class));
        doReturn(TestUtils.createEmployees(1).get(0)).when(employeeRepository).save(any(Employee.class));
        doNothing().when(kafkaProducerService).sendMessage(any(String.class));

        MvcResult result = mockMvc.perform(post(AppConstants.API + AppConstants.EMPLOYEE)
                .content(TestUtils.createEmployee("Create"))
                .with(user(userName).password(password).roles(roleUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertNotNull(result.getResponse().getContentAsString());
    }

    /*** Update Employee Test ****/
    @Test
    public void employeeUUIDNotFound() throws Exception {
        doReturn(Optional.empty()).when(employeeRepository).findById(any(UUID.class));
        doReturn(null).when(employeeRepository).findByEmail(any(String.class));
        doReturn(Optional.of(TestUtils.createDepartment())).when(departmentRepository).findById(any(Long.class));
        doReturn(TestUtils.createEmployees(1).get(0)).when(employeeRepository).save(any(Employee.class));
        doNothing().when(kafkaProducerService).sendMessage(any(String.class));

        MvcResult result = mockMvc.perform(put(AppConstants.API + AppConstants.EMPLOYEE)
                .content(TestUtils.createEmployee("Update"))
                .with(user(userName).password(password).roles(roleUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        assertEquals(AppConstants.EMPLOYEE_NOT_EXISTS, result.getResponse().getContentAsString());
    }

    @Test
    public void employeeWithDuplicateEmail() throws Exception {
        doReturn(Optional.of(TestUtils.createEmployees(1).get(0))).when(employeeRepository).findById(any(UUID.class));
        doReturn(TestUtils.createEmployees(1).get(0)).when(employeeRepository).findByEmail(any(String.class));
        doReturn(Optional.of(TestUtils.createDepartment())).when(departmentRepository).findById(any(Long.class));
        doReturn(TestUtils.createEmployees(1).get(0)).when(employeeRepository).save(any(Employee.class));
        doNothing().when(kafkaProducerService).sendMessage(any(String.class));

        MvcResult result = mockMvc.perform(put(AppConstants.API + AppConstants.EMPLOYEE)
                .content(TestUtils.createEmployee("Update"))
                .with(user(userName).password(password).roles(roleUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        assertEquals(AppConstants.EMAIL_EXISTS, response.get(0));
    }

    @Test
    public void employeeWithValidUpdateObject() throws Exception {
        Employee employee = TestUtils.createEmployees(1).get(0);
        doReturn(Optional.of(employee)).when(employeeRepository).findById(any(UUID.class));
        doReturn(null).when(employeeRepository).findByEmail(any(String.class));
        doReturn(Optional.of(TestUtils.createDepartment())).when(departmentRepository).findById(any(Long.class));
        doReturn(employee).when(employeeRepository).save(any(Employee.class));
        doNothing().when(kafkaProducerService).sendMessage(any(String.class));

        MvcResult result = mockMvc.perform(put(AppConstants.API + AppConstants.EMPLOYEE)
                .content(TestUtils.createEmployee("Update"))
                .with(user(userName).password(password).roles(roleUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        assertNotNull(response);
    }

    /***Get Employee By ID***/
    @Test
    public void employeeIdNotFound() throws Exception {
        doReturn(Optional.empty()).when(employeeRepository).findById(any(UUID.class));

        MvcResult result = mockMvc.perform(get(AppConstants.API + AppConstants.EMPLOYEE + "/" + UUID.randomUUID())
                .with(user(userName).password(password).roles(roleUser))
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        assertEquals(AppConstants.EMPLOYEE_NOT_EXISTS, result.getResponse().getContentAsString());
    }

    @Test
    public void getEmployeeById() throws Exception {
        Employee employee = TestUtils.createEmployees(1).get(0);
        doReturn(Optional.of(employee)).when(employeeRepository).findById(any(UUID.class));

        MvcResult result = mockMvc.perform(get(AppConstants.API + AppConstants.EMPLOYEE + "/" + employee.getUuid())
                .with(user(userName).password(password).roles(roleUser))
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        assertEquals(employee.getEmail(), response.getString("email"));
    }

    /*** Delete Employee By Id **/
    @Test
    public void deleteUnKnownEmployee() throws Exception {
        doReturn(Optional.empty()).when(employeeRepository).findById(any(UUID.class));

        MvcResult result = mockMvc.perform(delete(AppConstants.API + AppConstants.EMPLOYEE + "/" + UUID.randomUUID())
                .with(user(userName).password(password).roles(roleUser))
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        assertEquals(AppConstants.EMPLOYEE_NOT_EXISTS, result.getResponse().getContentAsString());
    }


    @Test
    public void deleteEmployeeById() throws Exception {
        doReturn(Optional.of(TestUtils.createEmployees(1).get(0))).when(employeeRepository).findById(any(UUID.class));
        doNothing().when(employeeRepository).deleteById(any(UUID.class));
        doNothing().when(kafkaProducerService).sendMessage(any(String.class));

        MvcResult result = mockMvc.perform(delete(AppConstants.API + AppConstants.EMPLOYEE + "/" + UUID.randomUUID())
                .with(user(userName).password(password).roles(roleUser))
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
    }

}
