package com.ofg.homework.loan.risk;

import com.ofg.homework.loan.model.LoanRequest;

public interface Risk {

    public boolean isRiskTooHigh(LoanRequest request);
}
