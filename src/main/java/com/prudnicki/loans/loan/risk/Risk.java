package com.prudnicki.loans.loan.risk;

import com.prudnicki.loans.loan.model.LoanRequest;

public interface Risk {

    boolean isRiskTooHigh(LoanRequest request);
}
