package com.ofg.homework.loan.interest;

import com.ofg.homework.loan.model.LoanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ConstInterestStrategy implements InterestCalculationStrategy {

    private BigDecimal defaultInterest;

    @Autowired
    public ConstInterestStrategy(@Value("${loan.default_interest}") BigDecimal returnedInterest) {
        this.defaultInterest = returnedInterest;
    }

    @Override
    public BigDecimal calculateInterest(LoanRequest request) {
        return defaultInterest;
    }

}
