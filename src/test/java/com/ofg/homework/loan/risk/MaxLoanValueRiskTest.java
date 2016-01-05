package com.ofg.homework.loan.risk;

import com.ofg.homework.loan.model.LoanRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import static com.ofg.homework.loan.TestDataUtil.createLoanRequest;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class MaxLoanValueRiskTest {

    private static final BigDecimal MAX_LOAN_VALUE = BigDecimal.valueOf(10.00);
    private MaxLoanValueRisk risk;
    private BigDecimal loanAmount;
    private boolean expectedOutcome;

    public MaxLoanValueRiskTest(BigDecimal loanAmount, boolean expectedOutcome) {
        this.loanAmount = loanAmount;
        this.expectedOutcome = expectedOutcome;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                //loan amount, expected risk outcome
                {BigDecimal.valueOf(9.99), false},
                {BigDecimal.valueOf(10.00), false},
                {BigDecimal.valueOf(10.01), true}
        });
    }

    @Before
    public void setUp() throws Exception {
        risk = new MaxLoanValueRisk(MAX_LOAN_VALUE);
    }

    @Test
    public void shouldCheckForMaxLoanAmount() {
        //given
        LoanRequest request = createLoanRequest();
        request.setAmount(loanAmount);

        //when
        boolean result = risk.isRiskTooHigh(request);

        //then
        assertEquals("Invalid result", expectedOutcome, result);
    }
}