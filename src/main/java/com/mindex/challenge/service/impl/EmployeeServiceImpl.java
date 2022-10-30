package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    /*
     * This service have a bit over head due to the recursive request to database. This is potential candidate for caching.
     * In this scope of quick project, let not consider it here.
     */
    public ReportingStructure getReportStructure(String id) {
        LOG.debug("Get report structure of employee with id [{}]", id);
        Employee employee = this.read(id);
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(getNumberOfReport(employee));
        return reportingStructure;
    }

    private Integer getNumberOfReport(Employee employee){
        if(employee.getDirectReports() == null)
            return 0;
        Integer count = employee.getDirectReports().size();
        for(Employee reporter: employee.getDirectReports()){
            Employee reporterInfo = this.read(reporter.getEmployeeId());
            count += getNumberOfReport(reporterInfo);
        }
        return count;
    }

}
