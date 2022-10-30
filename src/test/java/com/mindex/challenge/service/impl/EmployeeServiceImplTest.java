package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String employeeReportUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeReportUrl = "http://localhost:" + port + "/employee/{id}/report";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    @Test
    public void testGetReportStructure() {
        // Init data
        Employee developer = new Employee();
        developer.setFirstName("Hana");
        developer.setLastName("Jim");
        developer.setDepartment("Engineering");
        developer.setPosition("Development Engineer");
        Employee createdDeveloper = restTemplate.postForEntity(employeeUrl, developer, Employee.class).getBody();

        Employee manager = new Employee();
        manager.setFirstName("John");
        manager.setLastName("Doe");
        manager.setDepartment("Engineering");
        manager.setPosition("Development Manager");
        manager.setDirectReports(new ArrayList<>());
        manager.getDirectReports().add(createdDeveloper);
        Employee createdManager = restTemplate.postForEntity(employeeUrl, manager, Employee.class).getBody();

        Employee director = new Employee();
        director.setFirstName("Ray");
        director.setLastName("Doe");
        director.setDepartment("Engineering");
        director.setPosition("Development Director");
        director.setDirectReports(new ArrayList<>());
        director.getDirectReports().add(createdManager);
        Employee createdDirector = restTemplate.postForEntity(employeeUrl, director, Employee.class).getBody();

        // Get report check
        ReportingStructure reportInfo = restTemplate.getForEntity(employeeReportUrl, ReportingStructure.class, createdDirector.getEmployeeId()).getBody();
        assertNotNull(reportInfo.getEmployee());
        assertEquals(reportInfo.getEmployee().getEmployeeId(), createdDirector.getEmployeeId());
        assertEquals(reportInfo.getNumberOfReports(), Integer.valueOf(2));
    }
}
