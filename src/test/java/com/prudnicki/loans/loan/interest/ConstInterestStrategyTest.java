package com.prudnicki.loans.loan.interest;

import com.prudnicki.loans.loan.model.LoanRequest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.prudnicki.loans.loan.TestDataUtil.createLoanRequest;
import static org.junit.Assert.*;

public class ConstInterestStrategyTest {

    private static final BigDecimal DEFAULT_INTEREST = BigDecimal.valueOf(12.13);

    private ConstInterestStrategy strategy;

    @Before
    public void setUp() {
        strategy = new ConstInterestStrategy(DEFAULT_INTEREST);
    }

    @Test
    public void shouldReturnDefaultInterest() {
        //given
        LoanRequest request = createLoanRequest();

        //when
        BigDecimal interest = strategy.calculateInterest(request);

        //then
        assertEquals("Invalid interest", DEFAULT_INTEREST, interest);
    }


}