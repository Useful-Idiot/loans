package com.ofg.homework.loan.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Extension extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal interestBefore;

    private BigDecimal interestAfter;

    private LocalDateTime termBefore;

    private LocalDateTime termAfter;

    @ManyToOne
    @JsonIgnore
    private Loan loan;

    public Extension() {}

    public Extension(Loan loan, BigDecimal interestAfter, LocalDateTime termAfter) {
        this.termBefore = loan.getTerm();
        this.interestBefore = loan.getInterest();
        this.termAfter = termAfter;
        this.interestAfter = interestAfter;
        this.loan = loan;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getInterestBefore() {
        return interestBefore;
    }

    public void setInterestBefore(BigDecimal interestBefore) {
        this.interestBefore = interestBefore;
    }

    public BigDecimal getInterestAfter() {
        return interestAfter;
    }

    public void setInterestAfter(BigDecimal interestAfter) {
        this.interestAfter = interestAfter;
    }

    public LocalDateTime getTermBefore() {
        return termBefore;
    }

    public void setTermBefore(LocalDateTime termBefore) {
        this.termBefore = termBefore;
    }

    public LocalDateTime getTermAfter() {
        return termAfter;
    }

    public void setTermAfter(LocalDateTime termAfter) {
        this.termAfter = termAfter;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }
}
