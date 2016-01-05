package com.prudnicki.loans.loan.service;

import com.prudnicki.loans.loan.model.Extension;
import com.prudnicki.loans.loan.model.Loan;
import com.prudnicki.loans.loan.repository.ExtensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class ExtensionService {

    private BigDecimal extensionInterestMultiplier;
    private Integer extensionDays;
    private ExtensionRepository repository;

    @Autowired
    public ExtensionService(@Value("${loan.extension_multiplier}") BigDecimal extensionInterestMultiplier,
                            @Value("${loan.extension_days}") Integer extensionDays, ExtensionRepository repository) {
        this.extensionInterestMultiplier = extensionInterestMultiplier;
        this.extensionDays = extensionDays;
        this.repository = repository;
    }


    @Transactional
    public Extension extendLoan(Loan loan) {
        BigDecimal interestAfterExtension = loan.getInterest().multiply(extensionInterestMultiplier);
        LocalDateTime termAfterExtension = loan.getTerm().plusDays(extensionDays);

        Extension extension = new Extension(loan, interestAfterExtension, termAfterExtension);
        loan.extendWith(extension);

        return repository.save(extension);
    }

}
