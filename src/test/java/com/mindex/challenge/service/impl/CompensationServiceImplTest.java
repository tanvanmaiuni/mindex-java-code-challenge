package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationByEmployeeUrl;

    @Autowired
    private CompensationService compensationService;
    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationByEmployeeUrl = "http://localhost:" + port + "/compensation/byEmployee?employeeId={id}";
    }

    @Test
    public void testCreateRead() {
        Employee employee = employeeService.read("16a596ae-edd3-4847-99fe-c4518e82c86f");
        Compensation compensationOne = new Compensation();
        compensationOne.setEmployee(employee);
        compensationOne.setSalary(BigDecimal.valueOf(120000));
        compensationOne.setEffectiveDate(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));

        Compensation compensationTwo = new Compensation();
        compensationTwo.setEmployee(employee);
        compensationTwo.setSalary(BigDecimal.valueOf(200000));
        compensationTwo.setEffectiveDate(ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));

        // Create checks
        Compensation createdCompensationOne = restTemplate.postForEntity(compensationUrl, compensationOne, Compensation.class).getBody();
        assertNotNull(createdCompensationOne);
        assertCompensationEquivalence(compensationOne, createdCompensationOne);

        Compensation createdCompensationTwo = restTemplate.postForEntity(compensationUrl, compensationTwo, Compensation.class).getBody();
        assertNotNull(createdCompensationTwo);
        assertCompensationEquivalence(compensationTwo, createdCompensationTwo);


        // Read checks
        List<Compensation> readCompensation = restTemplate.exchange(
                compensationByEmployeeUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Compensation>>() {
                },
                employee.getEmployeeId()
        ).getBody();
        assertEquals(readCompensation.size(), 2);
        assertCompensationEquivalence(readCompensation.get(0), createdCompensationTwo);
        assertCompensationEquivalence(readCompensation.get(1), createdCompensationOne);
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertNotNull(expected.getEmployee());
        assertEquals(expected.getEmployee().getEmployeeId(), actual.getEmployee().getEmployeeId());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertTrue(actual.getEffectiveDate().isEqual(expected.getEffectiveDate()));
    }

}
