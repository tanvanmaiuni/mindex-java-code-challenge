package com.mindex.challenge.dao;

import com.mindex.challenge.data.Compensation;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompensationRepository extends MongoRepository<Compensation, String> {
    /*
    Both queries are work fine. In this case the second query is more verbose and easier to read.
     */
    List<Compensation> findAllByEmployeeEmployeeIdOrderByEffectiveDateDesc(String employeeId);
    @Query("{ 'employee.employeeId' : :#{#employeeId} }")
    List<Compensation> findAllByEmployeeId(String employeeId, Sort sort);
}
