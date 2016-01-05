package com.prudnicki.loans.loan.risk;

import com.prudnicki.loans.loan.model.LoanRequest;
import com.prudnicki.loans.loan.repository.LoanRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TooManyAttemptsFromSingleIpRisk implements Risk {

    private Integer requestsLimit;

    private LoanRequestRepository repository;

    @Autowired
    public TooManyAttemptsFromSingleIpRisk(@Value("${risk.request.daily_limit}") Integer requestLimit,
                                           LoanRequestRepository repository) {
        this.requestsLimit = requestLimit;
        this.repository = repository;
    }

    @Override
    public boolean isRiskTooHigh(LoanRequest request) {
        Long requestsCount = repository.countByIpAddressForDay(request.getIpAddress(),
                request.getCreatedDate());
        return requestsCount > requestsLimit;
    }
}
