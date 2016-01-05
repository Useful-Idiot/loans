package com.ofg.homework.loan.service;

import com.ofg.homework.loan.model.Extension;
import com.ofg.homework.loan.model.Loan;
import com.ofg.homework.loan.repository.ExtensionRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.ofg.homework.loan.TestDataUtil.createLoan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ExtensionServiceTest {

    private static final BigDecimal EXTENSION_MULTIPLIER = BigDecimal.valueOf(1.5);
    private static final int EXTENSION_DAYS = 7;

    private ExtensionService service;

    @Mock
    private ExtensionRepository repository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new ExtensionService(EXTENSION_MULTIPLIER, EXTENSION_DAYS, repository);
    }

    @Test
    public void shouldExtendLoan() {
        //given
        Loan loan = createLoan();
        LocalDateTime initialLoanTerm = loan.getTerm();
        BigDecimal initialLoanInterest = loan.getInterest();
        when(repository.save(any(Extension.class))).then(returnsFirstArg());

        //when
        Extension extension = service.extendLoan(loan);

        //then
        BigDecimal expectedNewInterest = initialLoanInterest.multiply(EXTENSION_MULTIPLIER);
        LocalDateTime expectedNewTerm = initialLoanTerm.plusDays(EXTENSION_DAYS);
        assertNotNull("Extension not found", extension);
        assertEquals("Invalid interest before", initialLoanInterest, extension.getInterestBefore());
        assertEquals("Invalid interest after", expectedNewInterest, extension.getInterestAfter());
        assertEquals("Invalid term before", initialLoanTerm, extension.getTermBefore());
        assertEquals("Invalid term after", expectedNewTerm, extension.getTermAfter());
        assertEquals("Invalid Loan interest", expectedNewInterest, extension.getLoan().getInterest());
        assertEquals("Invalid Loan term", expectedNewTerm, extension.getLoan().getTerm());
    }

}