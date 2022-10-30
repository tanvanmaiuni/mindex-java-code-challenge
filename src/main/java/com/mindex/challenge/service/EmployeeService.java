package com.mindex.challenge.service;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;

public interface EmployeeService {
    Employee create(Employee employee);
    Employee read(String id);
    Employee update(Employee employee);
    /*
    This method a bit of scope of employee.
    This is potential to split into another dedicate ReportService if the scope about the reporting expand in the future.
    For now, to keep it simple, let put together with employee
     */
    ReportingStructure getReportStructure(String id);
}
