package com.ofg.homework.loan.risk;

import com.ofg.homework.loan.model.LoanRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import static com.ofg.homework.loan.TestDataUtil.createLoanRequest;
import static org.junit.Assert.*;

@RunWith(Parameterized.class )
public class MaxLoanAtNightRiskTest {

    private static final BigDecimal MAX_LOAN_VALUE = BigDecimal.valueOf(10.00);
    private static final Integer HOURS_TILL_NIGHTS_END = 6;
    private MaxLoanAtNightRisk risk;
    private BigDecimal loanAmount;
    private LocalDateTime requestTime;
    private boolean expectedOutcome;

    public MaxLoanAtNightRiskTest(BigDecimal loanAmount, LocalDateTime requestTime, boolean expectedOutcome) {
        this.loanAmount = loanAmount;
        this.requestTime = requestTime;
        this.expectedOutcome = expectedOutcome;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                //loan amount, request time, expected risk outcome
                {BigDecimal.valueOf(9.99), timeOf(00, 00), false},
                {BigDecimal.valueOf(9.99), timeOf(03, 30), false},
                {BigDecimal.valueOf(9.99), timeOf(06, 00), false},
                {BigDecimal.valueOf(9.99), timeOf(06, 15), false},

                {BigDecimal.valueOf(10.00), timeOf(00, 00), true},
                {BigDecimal.valueOf(11.22), timeOf(03, 30), true},
                {BigDecimal.valueOf(10.00), timeOf(06, 00), true},

                {BigDecimal.valueOf(10.00), timeOf(06, 32), false},
                {BigDecimal.valueOf(11.22), timeOf(06, 32), false},
                {BigDecimal.valueOf(09.22), timeOf(06, 32), false},
        });
    }

    private static LocalDateTime timeOf(int hour, int minute) {
        return LocalDateTime.of(2015, 12, 24, hour, minute, 0);
    }

    @Before
    public void setUp() throws Exception {
        risk = new MaxLoanAtNightRisk(MAX_LOAN_VALUE, HOURS_TILL_NIGHTS_END);
    }

    @Test
    public void shoulCheckForMaxLoanAtNight() {
        //given
        LoanRequest request = createLoanRequest();
        request.setAmount(loanAmount);
        request.setCreatedDate(requestTime);

        //when
        boolean result = risk.isRiskTooHigh(request);

        //then
        assertEquals("Invalid result", expectedOutcome, result);
    }


}