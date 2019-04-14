package com.employee.service.app;

import com.employee.service.app.models.Department;
import com.employee.service.app.models.Employee;
import com.employee.service.app.utils.TestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResourceAccessException;

import java.net.URL;
import java.time.LocalDate;

import static com.employee.service.app.utils.AppConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@EnableKafka
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = KAFKA_TOPIC, controlledShutdown = true, brokerProperties = {"listeners=PLAINTEXT://localhost:3333", "port=3333"})
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationIntegrationTests {

    private static final String LOCAL_HOST = "http://localhost:";

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Value(USER_NAME)
    private String userName;

    @Value(PASSWORD)
    private String password;

    @Before
    public void setUp() throws Exception {
        this.base = new URL(LOCAL_HOST + port + API);
    }

    //Department With Empty Name
    @Test
    public void test1() throws Exception {
        HttpEntity<Department> request = new HttpEntity<>(new Department());

        ResponseEntity<String> response = template.postForEntity(base.toString() + DEPARTMENT, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        JSONArray result = new JSONArray(response.getBody());
        assertEquals(NAME_REQUIRED, result.get(0));
    }

    //Department created successfully
    @Test
    public void test2() throws Exception {
        Department department = TestUtils.createDepartment();
        HttpEntity<Department> request = new HttpEntity<>(department);

        ResponseEntity<String> response = template.postForEntity(base.toString() + DEPARTMENT, request, String.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        JSONObject result = new JSONObject(response.getBody());
        assertEquals(department.getName(), result.getString("name"));
    }

    //Duplicate department name check
    @Test
    public void test3() throws Exception {
        Department department = TestUtils.createDepartment();
        HttpEntity<Department> request = new HttpEntity<>(department);

        ResponseEntity<String> response = template.postForEntity(base.toString() + DEPARTMENT, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        JSONArray result = new JSONArray(response.getBody());
        assertEquals(DEPARTMENT_EXISTS, result.get(0));
    }

    //Check for unauthorized
    @Test(expected = ResourceAccessException.class)
    public void test4() throws Exception {
        Employee employee = TestUtils.createEmployees(1).get(0);
        HttpEntity<?> request = new HttpEntity<>(employee);

        template.postForEntity(base.toString() + EMPLOYEE, request, String.class);
    }

    //Email Required Validations
    @Test
    public void test5() throws Exception {
        HttpEntity<Employee> request = new HttpEntity<>(new Employee());

        ResponseEntity<String> response = template.withBasicAuth(userName, password).postForEntity(base.toString() + EMPLOYEE, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        JSONArray result = new JSONArray(response.getBody());
        assertEquals(EMAIL_REQUIRED, result.get(0));
    }

    //Email Invalid Validations
    @Test
    public void test6() throws Exception {
        Employee employee = new Employee();
        employee.setEmail("aaaa");
        HttpEntity<Employee> request = new HttpEntity<>(employee);

        ResponseEntity<String> response = template.withBasicAuth(userName, password).postForEntity(base.toString() + EMPLOYEE, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        JSONArray result = new JSONArray(response.getBody());
        assertEquals(EMAIL_INVALID, result.get(0));
    }

    //Save Employee
    @Test
    public void test7() throws Exception {
        Employee employee = new Employee();
        employee.setEmail("test@gmail.com");
        HttpEntity<Employee> request = new HttpEntity<>(employee);

        ResponseEntity<String> response = template.withBasicAuth(userName, password).postForEntity(base.toString() + EMPLOYEE, request, String.class);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        JSONObject result = new JSONObject(response.getBody());
        assertNotNull(result);
    }

    //Duplicate Email
    @Test
    public void test8() throws Exception {
        Employee employee = new Employee();
        employee.setEmail("test@gmail.com");
        employee.setBirthday(LocalDate.of(1991, 8, 03));
        employee.setDepartmentId(1L);
        HttpEntity<Employee> request = new HttpEntity<>(employee);

        ResponseEntity<String> response = template.withBasicAuth(userName, password).postForEntity(base.toString() + EMPLOYEE, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        JSONArray result = new JSONArray(response.getBody());
        assertEquals(EMAIL_EXISTS, result.get(0));
    }

    //Employee with non exist department
    @Test
    public void test9() throws Exception {
        Employee employee = new Employee();
        employee.setEmail("test1@gmail.com");
        employee.setDepartmentId(0L);
        HttpEntity<Employee> request = new HttpEntity<>(employee);

        ResponseEntity<String> response = template.withBasicAuth(userName, password).postForEntity(base.toString() + EMPLOYEE, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        JSONArray result = new JSONArray(response.getBody());
        assertEquals(DEPARTMENT_NOT_EXISTS, result.get(0));
    }

    //Update Employee with unknown UUID
    @Test
    public void test10() throws Exception {
        HttpEntity<Employee> request = new HttpEntity<>(TestUtils.createEmployees(1).get(0));

        ResponseEntity<String> response = template.withBasicAuth(userName, password).exchange(base.toString() + EMPLOYEE, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertEquals(EMPLOYEE_NOT_EXISTS, response.getBody());
    }

    //Update Employee with known UUID
    @Test
    public void test11() throws Exception {
        Employee employee = TestUtils.createEmployees(1).get(0);
        employee.setUuid(null);

        HttpEntity<Employee> request = new HttpEntity<>(employee);
        ResponseEntity<String> response = template.withBasicAuth(userName, password).exchange(base.toString() + EMPLOYEE, HttpMethod.POST, request, String.class);

        JSONObject employeeObject = new JSONObject(response.getBody());
        employee.setUuid(java.util.UUID.fromString(employeeObject.getString("uuid")));
        employee.setEmail("update@gmail.com");

        request = new HttpEntity<>(employee);
        response = template.withBasicAuth(userName, password).exchange(base.toString() + EMPLOYEE, HttpMethod.PUT, request, String.class);


        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        employeeObject = new JSONObject(response.getBody());
        System.out.println(employeeObject);
        assertEquals(employee.getEmail(), employeeObject.getString("email"));
    }

    //Get Employee Details with unknown UUID
    @Test
    public void test12() throws Exception {
        ResponseEntity<String> response = template.withBasicAuth(userName, password).getForEntity(base.toString() + EMPLOYEE + "/" + java.util.UUID.randomUUID(), String.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertEquals(EMPLOYEE_NOT_EXISTS, response.getBody());
    }

    //Get Employee Details with known UUID
    @Test
    public void test13() throws Exception {
        Employee employee = TestUtils.createEmployees(1).get(0);
        employee.setUuid(null);
        employee.setEmail("getemp@gmail.com");
        employee.setFullName("Test");
        employee.setBirthday(LocalDate.of(1991, 8, 19));

        HttpEntity<Employee> request = new HttpEntity<>(employee);
        ResponseEntity<String> response = template.withBasicAuth(userName, password).exchange(base.toString() + EMPLOYEE, HttpMethod.POST, request, String.class);

        JSONObject employeeObject = new JSONObject(response.getBody());
        String uuid = employeeObject.getString("uuid");

        response = template.withBasicAuth(userName, password).getForEntity(base.toString() + EMPLOYEE + "/" + uuid, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        JSONObject getEmployeeObject = new JSONObject(response.getBody());
        assertEquals(getEmployeeObject.getString("email"), employeeObject.getString("email"));
        assertEquals(getEmployeeObject.getString("fullName"), employeeObject.getString("fullName"));
    }

    //Delete Employee with unknown UUID
    @Test
    public void test14() throws Exception {
        ResponseEntity<String> response = template.withBasicAuth(userName, password).exchange(base.toString() + EMPLOYEE + "/" + java.util.UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertEquals(EMPLOYEE_NOT_EXISTS, response.getBody());
    }

    @Test
    public void test15() throws Exception {
        Employee employee = TestUtils.createEmployees(1).get(0);
        employee.setUuid(null);
        employee.setEmail("getemployee@gmail.com");
        employee.setFullName("Test");
        employee.setBirthday(LocalDate.of(1991, 8, 19));

        HttpEntity<Employee> request = new HttpEntity<>(employee);
        ResponseEntity<String> response = template.withBasicAuth(userName, password).exchange(base.toString() + EMPLOYEE, HttpMethod.POST, request, String.class);

        JSONObject employeeObject = new JSONObject(response.getBody());
        String uuid = employeeObject.getString("uuid");

        response = template.withBasicAuth(userName, password).exchange(base.toString() + EMPLOYEE + "/" + uuid, HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCodeValue());
    }

}
