package com.ofg.homework.loan.risk;

import com.ofg.homework.loan.TestDataUtil;
import com.ofg.homework.loan.repository.LoanRequestRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import static com.ofg.homework.loan.TestDataUtil.createLoanRequest;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class TooManyAttemptsFromSingleIpRiskTest {

    private static final Integer REQUEST_LIMIT = 3;
    @Mock
    private LoanRequestRepository repository;
    private TooManyAttemptsFromSingleIpRisk risk;
    private long countCallResult;
    private boolean expectedOutcome;

    public TooManyAttemptsFromSingleIpRiskTest(long countCallResult, boolean expectedOutcome) {
        this.countCallResult = countCallResult;
        this.expectedOutcome = expectedOutcome;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                //count call result, expected risk outcome
                {2L, false},
                {REQUEST_LIMIT.longValue(), false},
                {5L, true}
        });
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        risk = new TooManyAttemptsFromSingleIpRisk(REQUEST_LIMIT, repository);
    }

    @Test
    public void shouldCheckForDailyRequestCountLimit() {
        //given
        when(repository.countByIpAddressForDay(eq(TestDataUtil.DEFAULT_IP_ADDRESS), any(LocalDateTime.class)))
                .thenReturn(countCallResult);

        //when
        boolean result = risk.isRiskTooHigh(createLoanRequest());

        //then
        assertEquals("Invalid result", expectedOutcome, result);
    }
}