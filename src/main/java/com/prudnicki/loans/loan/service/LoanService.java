package com.prudnicki.loans.loan.service;

import com.prudnicki.loans.loan.interest.InterestCalculationStrategy;
import com.prudnicki.loans.loan.model.Loan;
import com.prudnicki.loans.loan.model.LoanRequest;
import com.prudnicki.loans.loan.repository.LoanRepository;
import com.prudnicki.loans.loan.repository.LoanRequestRepository;
import com.prudnicki.loans.loan.risk.RiskAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class LoanService {

    private RiskAnalyser analyser;
    private InterestCalculationStrategy interestStrategy;
    private LoanRepository repository;
    private LoanRequestRepository requestRepository;

    @Autowired
    public LoanService(RiskAnalyser analyser, InterestCalculationStrategy interestStrategy,
                       LoanRepository repository, LoanRequestRepository requestRepository) {
        this.analyser = analyser;
        this.interestStrategy = interestStrategy;
        this.repository = repository;
        this.requestRepository = requestRepository;
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Loan createLoan(LoanRequest request) {
        LoanRequest savedRequest = requestRepository.saveAndFlush(request);
        return performRiskAnalysisAndSaveLoan(savedRequest);
    }

    private Loan performRiskAnalysisAndSaveLoan(LoanRequest request) {
        boolean riskTooHigh = analyser.isRiskTooHigh(request);
        if (riskTooHigh) {
            throw new RiskTooHightException();
        }
        BigDecimal interest = interestStrategy.calculateInterest(request);
        Loan newLoan = new Loan(request, interest);
        return repository.save(newLoan);
    }

}
