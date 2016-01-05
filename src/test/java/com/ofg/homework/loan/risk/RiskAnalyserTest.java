package com.ofg.homework.loan.risk;

import com.ofg.homework.loan.model.LoanRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;

import static com.ofg.homework.loan.TestDataUtil.createLoanRequest;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class RiskAnalyserTest {

    @Mock
    private Risk firstRisk;
    @Mock
    private Risk secondRisk;

    private boolean firstRiskReturnValue;
    private boolean secondRiskReturnValue;
    private boolean expectedOutcome;
    private RiskAnalyser analyser;

    public RiskAnalyserTest(boolean firstRiskReturnValue, boolean secondRiskReturnValue, boolean expectedOutcome) {
        this.firstRiskReturnValue = firstRiskReturnValue;
        this.secondRiskReturnValue = secondRiskReturnValue;
        this.expectedOutcome = expectedOutcome;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                //first risk return value, second risk return value, expected outcome
                {false, false, false},
                {false, true, true},
                {true, true, true}
        });
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        analyser = new RiskAnalyser(Arrays.asList(firstRisk, secondRisk));
    }

    @Test
    public void shouldCheckIfRiskIsTooHigh() {
        //given
        LoanRequest request = createLoanRequest();
        when(firstRisk.isRiskTooHigh(request)).thenReturn(firstRiskReturnValue);
        when(secondRisk.isRiskTooHigh(request)).thenReturn(secondRiskReturnValue);

        //when
        boolean result = analyser.isRiskTooHigh(request);

        //then
        assertEquals("Invalid result", expectedOutcome, result);
    }

}