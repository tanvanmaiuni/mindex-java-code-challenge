package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
This API should gate by security to check permission to read and write compensation.
In this scope of quick project, let not consider it here.
 */
@RequestMapping("/compensation")
@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    /*
    The input should be validated in order to avoid bad data.
    In this scope of quick project, let not consider it here.
     */
    @PostMapping()
    public Compensation create(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for [{}]", compensation);

        return compensationService.create(compensation);
    }

    /*
    Depend on the business clarify, one employee may have one or more compensation records.
    Let assume we can keep track all compensation changes of an employee.
    The return will be order by effective date
     */
    @GetMapping("/byEmployee")
    public List<Compensation> getByEmployee(@RequestParam String employeeId) {
        LOG.debug("Received read compensation request for id [{}]", employeeId);

        return compensationService.read(employeeId);
    }

}
