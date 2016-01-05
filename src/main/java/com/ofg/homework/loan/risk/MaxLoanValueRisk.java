package com.ofg.homework.loan.risk;

import com.ofg.homework.loan.model.LoanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MaxLoanValueRisk implements Risk {

    private BigDecimal maxLoanValue;

    @Autowired
    public MaxLoanValueRisk(@Value("${risk.request.max_value}") BigDecimal maxLoanValue) {
        this.maxLoanValue = maxLoanValue;
    }

    @Override
    public boolean isRiskTooHigh(LoanRequest request) {
        return maxLoanValue.compareTo(request.getAmount()) < 0;
    }

}
