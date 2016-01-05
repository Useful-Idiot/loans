package com.ofg.homework.loan.interest;

import com.ofg.homework.loan.model.LoanRequest;

import java.math.BigDecimal;

public interface InterestCalculationStrategy {

    public BigDecimal calculateInterest(LoanRequest request);

}
