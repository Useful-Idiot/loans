package com.prudnicki.loans.loan.interest;

import com.prudnicki.loans.loan.model.LoanRequest;

import java.math.BigDecimal;

public interface InterestCalculationStrategy {

    BigDecimal calculateInterest(LoanRequest request);

}
