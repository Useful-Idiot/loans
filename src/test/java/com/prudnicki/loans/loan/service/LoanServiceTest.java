package com.prudnicki.loans.loan.service;

import com.prudnicki.loans.loan.interest.ConstInterestStrategy;
import com.prudnicki.loans.loan.model.Loan;
import com.prudnicki.loans.loan.model.LoanRequest;
import com.prudnicki.loans.loan.repository.LoanRepository;
import com.prudnicki.loans.loan.repository.LoanRequestRepository;
import com.prudnicki.loans.loan.risk.RiskAnalyser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.prudnicki.loans.loan.TestDataUtil.createLoanRequest;
import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class LoanServiceTest {

    private static final BigDecimal INTEREST = BigDecimal.valueOf(17.14);

    @Mock
    private RiskAnalyser analyser;
    @Mock
    private LoanRepository repository;
    @Mock
    private LoanRequestRepository requestRepository;

    private LoanService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new LoanService(analyser, new ConstInterestStrategy(INTEREST), repository, requestRepository);
    }

    @Test(expected = RiskTooHightException.class)
    public void shouldRiseExceptionIfRiskTooHigh() {
        //given
        LoanRequest request = createLoanRequest();
        when(requestRepository.saveAndFlush(request)).then(returnsFirstArg());
        when(analyser.isRiskTooHigh(request)).thenReturn(true);

        //when
        service.createLoan(request);

        //then
        //exception expected
    }

    @Test
    public void shouldSaveLoan() {
        //given
        LoanRequest request = createLoanRequest();
        when(requestRepository.saveAndFlush(request)).then(returnsFirstArg());
        when(analyser.isRiskTooHigh(request)).thenReturn(false);
        when(repository.save(any(Loan.class))).then(returnsFirstArg());

        //when
        Loan loan = service.createLoan(request);

        //then
        assertEquals("Invalid amount", request.getAmount(), loan.getAmount());
        assertEquals("Invalid interest", INTEREST, loan.getInterest());
        assertEquals("Invalid first name", request.getFirstName(), loan.getCustomerFirstName());
        assertEquals("Invalid last name", request.getLastName(), loan.getCustomerLastName());
        assertEquals("Invalid pesel", request.getPesel(), loan.getCustomerPesel());
        //compare only with precision of day
        LocalDateTime expectedTerm = LocalDateTime.now().plusDays(request.getTerm()).truncatedTo(ChronoUnit.DAYS);
        assertEquals("Invalid term", expectedTerm, loan.getTerm().truncatedTo(ChronoUnit.DAYS));
    }

}