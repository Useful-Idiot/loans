package com.prudnicki.loans.loan;

import com.prudnicki.loans.loan.model.Extension;
import com.prudnicki.loans.loan.model.Loan;
import com.prudnicki.loans.loan.model.LoanRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TestDataUtil {

    public static final Long DEFAULT_ID = 10L;
    public static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(6000.29);
    public static final String DEFAULT_FIRST_NAME = "Jan";
    public static final String DEFAULT_LAST_NAME = "Kowalski";
    public static final Long DEFAULT_PESEL = 74082804472L;
    public static final String DEFAULT_IP_ADDRESS = "127.0.0.2";
    public static final int DEFAULT_TERM_OFFSET_IN_DAYS = 30;
    public static final BigDecimal DEFAULT_INTEREST = BigDecimal.valueOf(10.12);

    public static final BigDecimal DEFAULT_INTEREST_MULTIPLIER = BigDecimal.valueOf(1.5);
    public static final Integer DEFAULT_EXTENSION_DAYS = 7;


    public static LoanRequest createLoanRequest() {
        LoanRequest request = new LoanRequest();
        request.setId(DEFAULT_ID);
        request.setAmount(DEFAULT_AMOUNT);
        request.setFirstName(DEFAULT_FIRST_NAME);
        request.setLastName(DEFAULT_LAST_NAME);
        request.setPesel(DEFAULT_PESEL);
        request.setIpAddress(DEFAULT_IP_ADDRESS);
        request.setTerm(DEFAULT_TERM_OFFSET_IN_DAYS);
        return request;
    }

    public static Loan createLoan() {
        Loan loan = new Loan();
        loan.setId(DEFAULT_ID);
        loan.setAmount(DEFAULT_AMOUNT);
        loan.setCustomerFirstName(DEFAULT_FIRST_NAME);
        loan.setCustomerLastName(DEFAULT_LAST_NAME);
        loan.setCustomerPesel(DEFAULT_PESEL);
        loan.setInterest(DEFAULT_INTEREST);
        loan.setTerm(getDefaultTerm());
        return loan;
    }

    private static LocalDateTime getDefaultTerm() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(DEFAULT_TERM_OFFSET_IN_DAYS);
    }

    public static Extension createExtension() {
        Extension extension = new Extension();
        extension.setInterestBefore(DEFAULT_INTEREST);
        extension.setInterestAfter(DEFAULT_INTEREST.multiply(DEFAULT_INTEREST_MULTIPLIER));
        extension.setTermBefore(getDefaultTerm());
        extension.setTermAfter(getDefaultTerm().plusDays(DEFAULT_EXTENSION_DAYS));
        return extension;
    }

    public static Loan createLoanWithExtensions() {
        Loan loan = createLoan();
        extend(loan);
        extend(loan);
        return loan;
    }

    private static void extend(Loan loan) {
        BigDecimal interestAfterExtension = loan.getInterest().multiply(DEFAULT_INTEREST_MULTIPLIER);
        LocalDateTime termAfterExtension = loan.getTerm().plusDays(DEFAULT_EXTENSION_DAYS);

        Extension extension = new Extension(loan, interestAfterExtension, termAfterExtension);
        loan.extendWith(extension);
    }

}
