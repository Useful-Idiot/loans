package com.prudnicki.loans.loan.risk;

import com.prudnicki.loans.loan.model.LoanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RiskAnalyser {

    private List<Risk> risks;

    @Autowired
    public RiskAnalyser(List<Risk> risks) {
        this.risks = risks;
    }

    public boolean isRiskTooHigh(LoanRequest request) {
        return risks.stream().anyMatch(r -> r.isRiskTooHigh(request));
    }
}
