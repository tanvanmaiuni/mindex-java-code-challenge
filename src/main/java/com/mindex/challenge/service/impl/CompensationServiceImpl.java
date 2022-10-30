package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompensationServiceImpl implements CompensationService {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        compensation.setCompensationId(UUID.randomUUID().toString());
        compensationRepository.insert(compensation);

        return compensation;
    }

    @Override
    public List<Compensation> read(String employeeId) {
        LOG.debug("Read compensation with employeeId [{}]", employeeId);
        Sort sortedByDate = Sort.by(Sort.Direction.DESC, "effectiveDate");
        return compensationRepository.findAllByEmployeeId(employeeId, sortedByDate);
    }
}
